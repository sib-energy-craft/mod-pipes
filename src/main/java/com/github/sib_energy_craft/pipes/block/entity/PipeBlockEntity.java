package com.github.sib_energy_craft.pipes.block.entity;

import com.github.sib_energy_craft.pipes.block.PipeBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * @since 0.0.1
 * @author sibmaks
 */
public abstract class PipeBlockEntity<T extends PipeBlock>
        extends BlockEntity implements Inventory {
    private ItemStack storage;
    private Direction consumedDirection;

    private final T block;
    private int lastTicksToInsert;

    public PipeBlockEntity(@NotNull BlockEntityType<? extends PipeBlockEntity<T>> entityType,
                           @NotNull T block,
                           @NotNull BlockPos pos,
                           @NotNull BlockState state) {
        super(entityType, pos, state);
        this.block = block;
        this.storage = ItemStack.EMPTY;
        this.consumedDirection = Direction.UP;
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);
        storage = ItemStack.fromNbt(nbt.getCompound("storage"));
        consumedDirection = Direction.byName(nbt.getString("direction"));
    }

    @Override
    protected void writeNbt(@NotNull NbtCompound nbt) {
        super.writeNbt(nbt);
        var compound = nbt.getCompound("storage");
        storage.writeNbt(compound);
        nbt.putString("direction", consumedDirection.getName());
    }

    @Override
    public int size() {
        return 1;
    }

    @NotNull
    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (slot != 0 || amount <= 0) {
            return ItemStack.EMPTY;
        }
        return storage.split(amount);
    }

    public void setStack(@NotNull Direction direction,
                         @NotNull ItemStack stack) {
        this.consumedDirection = direction.getOpposite();
        this.storage = stack;
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
    }

    public static void serverTick(@NotNull World world,
                                  @NotNull BlockPos pos,
                                  @NotNull BlockState state,
                                  @NotNull PipeBlockEntity<?> blockEntity) {
        insertAndExtract(world, pos, state, blockEntity);
    }

    private static void insertAndExtract(@NotNull World world,
                                         @NotNull BlockPos pos,
                                         @NotNull BlockState state,
                                         @NotNull PipeBlockEntity<?> blockEntity) {
        if (world.isClient) {
            return;
        }
        boolean modified = false;
        if(!blockEntity.isEmpty() && blockEntity.lastTicksToInsert > 0) {
            blockEntity.lastTicksToInsert--;
        }
        if (!blockEntity.isEmpty() && blockEntity.lastTicksToInsert <= 0) {
            modified = insert(world, pos, blockEntity, blockEntity.consumedDirection);
            blockEntity.lastTicksToInsert = blockEntity.block.getTicksToInsert();
        }
        if (modified) {
            markDirty(world, pos, state);
        }
    }

    private static boolean insert(@NotNull World world,
                                  @NotNull BlockPos pos,
                                  @NotNull Inventory inventory,
                                  @NotNull Direction consumedDirection) {
        var outputInventories = getOutputInventory(world, pos, consumedDirection);
        for (var entry : outputInventories.entrySet()) {
            var direction = entry.getKey();
            var outputInventory = entry.getValue();
            if (isInventoryFull(outputInventory, direction)) {
                continue;
            }
            for (int i = 0; i < inventory.size(); ++i) {
                var inventoryStack = inventory.getStack(i);
                if (inventoryStack.isEmpty()) {
                    continue;
                }
                var itemStack = inventoryStack.copy();
                var notTransferred = transfer(outputInventory, inventory.removeStack(i, 1), direction);
                if (notTransferred.isEmpty()) {
                    outputInventory.markDirty();
                    return true;
                }
                inventory.setStack(i, itemStack);
            }
        }
        return false;
    }

    @NotNull
    private static IntStream getAvailableSlots(@NotNull Inventory inventory,
                                               @NotNull Direction side) {
        if (inventory instanceof SidedInventory sidedInventory) {
            return IntStream.of(sidedInventory.getAvailableSlots(side));
        }
        return IntStream.range(0, inventory.size());
    }

    private static boolean isInventoryFull(@NotNull Inventory inventory,
                                           @NotNull Direction direction) {
        return getAvailableSlots(inventory, direction).allMatch(slot -> {
            var itemStack = inventory.getStack(slot);
            return itemStack.getCount() >= itemStack.getMaxCount();
        });
    }

    /*
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    public static ItemStack transfer(@NotNull Inventory to,
                                     @NotNull ItemStack stack,
                                     @Nullable Direction side) {
        if (to instanceof SidedInventory sidedInventory) {
            if (side != null) {
                int[] is = sidedInventory.getAvailableSlots(side);
                int i = 0;
                while (i < is.length) {
                    if (stack.isEmpty()) return stack;
                    stack = transfer(to, stack, is[i], side);
                    ++i;
                }
                return stack;
            }
        }
        int j = to.size();
        int i = 0;
        while (i < j) {
            if (stack.isEmpty()) {
                return stack;
            }
            stack = transfer(to, stack, i, side);
            ++i;
        }
        return stack;
    }

    private static boolean canInsert(@NotNull Inventory inventory,
                                     @NotNull ItemStack stack,
                                     int slot,
                                     @Nullable Direction side) {
        if (!inventory.isValid(slot, stack)) {
            return false;
        }
        return !(inventory instanceof SidedInventory sidedInventory) || sidedInventory.canInsert(slot, stack, side);
    }

    @NotNull
    private static ItemStack transfer(@NotNull Inventory to,
                                      @NotNull ItemStack stack,
                                      int slot,
                                      @Nullable Direction side) {
        var itemStack = to.getStack(slot);
        if (canInsert(to, stack, slot, side)) {
            boolean transferred = false;
            if (itemStack.isEmpty()) {
                if(to instanceof PipeBlockEntity<?> pipeBlockEntity) {
                    pipeBlockEntity.setStack(side, stack);
                } else {
                    to.setStack(slot, stack);
                }
                stack = ItemStack.EMPTY;
                transferred = true;
            } else if (canMergeItems(itemStack, stack)) {
                int i = stack.getMaxCount() - itemStack.getCount();
                int j = Math.min(stack.getCount(), i);
                stack.decrement(j);
                itemStack.increment(j);
            }
            if (transferred) {
                to.markDirty();
            }
        }
        return stack;
    }

    @NotNull
    private static Map<Direction, Inventory> getOutputInventory(@NotNull World world,
                                                                @NotNull BlockPos pos,
                                                                @NotNull Direction consumedDirection) {
        var inventories = new HashMap<Direction, Inventory>();
        for (var outputDirection : Direction.values()) {
            if(outputDirection == consumedDirection) {
                continue;
            }
            var inventory = getInventoryAt(world, pos.offset(outputDirection));
            if(inventory != null) {
                inventories.put(outputDirection, inventory);
            }
        }
        return inventories;
    }

    @Nullable
    public static Inventory getInventoryAt(@NotNull World world,
                                           @NotNull BlockPos pos) {
        return getInventoryAt(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    @Nullable
    private static Inventory getInventoryAt(@NotNull World world,
                                            double x,
                                            double y,
                                            double z) {
        Inventory inventory = null;
        var blockPos = BlockPos.ofFloored(x, y, z);
        var blockState = world.getBlockState(blockPos);
        var block = blockState.getBlock();
        if (block instanceof InventoryProvider inventoryProvider) {
            inventory = inventoryProvider.getInventory(blockState, world, blockPos);
        } else if (blockState.hasBlockEntity()) {
            var blockEntity = world.getBlockEntity(blockPos);
            if(blockEntity instanceof Inventory blockInventory) {
                inventory = blockInventory;
                if(inventory instanceof ChestBlockEntity && block instanceof ChestBlock chestBlock) {
                    inventory = ChestBlock.getInventory(chestBlock, blockState, world, blockPos, true);
                }
            }
        }
        if(inventory == null) {
            var box = new Box(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5);
            var list = world.getOtherEntities(null, box, EntityPredicates.VALID_INVENTORIES);
            if(!list.isEmpty()) {
                inventory = (Inventory) list.get(world.random.nextInt(list.size()));
            }
        }
        return inventory;
    }

    private static boolean canMergeItems(@NotNull ItemStack first,
                                         @NotNull ItemStack second) {
        if (!first.isOf(second.getItem())) {
            return false;
        }
        if (first.getDamage() != second.getDamage()) {
            return false;
        }
        if (first.getCount() > first.getMaxCount()) {
            return false;
        }
        return ItemStack.areNbtEqual(first, second);
    }

    @Override
    public boolean isEmpty() {
        return storage.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        if (slot != 0) {
            return ItemStack.EMPTY;
        }
        return storage;
    }

    @NotNull
    @Override
    public ItemStack removeStack(int slot) {
        if (slot != 0) {
            return ItemStack.EMPTY;
        }
        var was = storage;
        this.storage = ItemStack.EMPTY;
        return was;
    }

    @Override
    public void setStack(int slot,
                         @NotNull ItemStack stack) {
        if (slot != 0) {
            return;
        }
        this.storage = stack;
    }

    @Override
    public boolean canPlayerUse(@NotNull PlayerEntity player) {
        return false;
    }

    @Override
    public void clear() {
        this.storage = ItemStack.EMPTY;
    }
}
