package com.github.sib_energy_craft.pipes.block.entity;

import com.github.sib_energy_craft.pipes.block.ItemExtractorBlock;
import com.github.sib_energy_craft.pipes.load.Entities;
import com.github.sib_energy_craft.pipes.load.Screens;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.stream.IntStream;

/**
 * @since 0.0.1
 * @author sibmaks
 */
public class ItemExtractorBlockEntity extends LootableContainerBlockEntity implements ExtendedScreenHandlerFactory {
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(9, ItemStack.EMPTY);

    public ItemExtractorBlockEntity(@NotNull BlockPos pos,
                                    @NotNull BlockState state) {
        super(Entities.ITEM_EXTRACTOR, pos, state);
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!this.deserializeLootTable(nbt)) {
            Inventories.readNbt(nbt, this.inventory);
        }
    }

    @Override
    protected void writeNbt(@NotNull NbtCompound nbt) {
        super.writeNbt(nbt);
        if (!this.serializeLootTable(nbt)) {
            Inventories.writeNbt(nbt, this.inventory);
        }
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @NotNull
    @Override
    public ItemStack removeStack(int slot, int amount) {
        this.checkLootInteraction(null);
        return Inventories.splitStack(this.getInvStackList(), slot, amount);
    }

    @Override
    public void setStack(int slot, @NotNull ItemStack stack) {
        this.checkLootInteraction(null);
        this.getInvStackList().set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
    }

    @NotNull
    @Override
    protected Text getContainerName() {
        return Text.translatable("container.item_extractor");
    }

    public static void serverTick(@NotNull World world,
                                  @NotNull BlockPos pos,
                                  @NotNull BlockState state,
                                  @NotNull ItemExtractorBlockEntity blockEntity) {
        insertAndExtract(world, pos, state, blockEntity, () -> extract(world, blockEntity, state));
    }

    private static void insertAndExtract(@NotNull World world,
                                         @NotNull BlockPos pos,
                                         @NotNull BlockState state,
                                         @NotNull ItemExtractorBlockEntity blockEntity,
                                         @NotNull BooleanSupplier booleanSupplier) {
        if (world.isClient) {
            return;
        }
        boolean modified = false;
        if (!blockEntity.isEmpty()) {
            modified = insert(world, pos, state, blockEntity);
        }
        if (!blockEntity.isFull()) {
            modified |= booleanSupplier.getAsBoolean();
        }
        if (modified) {
            markDirty(world, pos, state);
        }
    }

    private boolean isFull() {
        for (var itemStack : this.inventory) {
            if (itemStack.isEmpty() || itemStack.getCount() != itemStack.getMaxCount()) {
                return false;
            }
        }
        return true;
    }

    private static boolean insert(@NotNull World world,
                                  @NotNull BlockPos pos,
                                  @NotNull BlockState state,
                                  @NotNull Inventory inventory) {
        var outputInventories = getOutputInventory(world, pos, state);
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

    private static boolean isInventoryEmpty(@NotNull Inventory inv,
                                            @NotNull Direction facing) {
        return getAvailableSlots(inv, facing).allMatch(slot -> inv.getStack(slot).isEmpty());
    }

    public static boolean extract(@NotNull World world,
                                  @NotNull ItemExtractorBlockEntity extractor,
                                  @NotNull BlockState blockState) {
        var direction = blockState.get(ItemExtractorBlock.FACING).getOpposite();
        var inventory = getInputInventory(world, extractor, direction);
        if (inventory != null) {
            if (isInventoryEmpty(inventory, direction)) {
                return false;
            }
            return getAvailableSlots(inventory, direction)
                    .anyMatch(slot -> extract(extractor, inventory, slot, direction));
        }
        return false;
    }

    private static boolean extract(@NotNull ItemExtractorBlockEntity extractor,
                                   @NotNull Inventory inventory,
                                   int slot,
                                   @NotNull Direction side) {
        var itemStack = inventory.getStack(slot);
        if (!itemStack.isEmpty() && canExtract(extractor, inventory, itemStack, slot, side)) {
            var copyStack = itemStack.copy();
            var notTransferred = transfer(extractor, inventory.removeStack(slot, 1), null);
            if (notTransferred.isEmpty()) {
                inventory.markDirty();
                return true;
            }
            inventory.setStack(slot, copyStack);
        }
        return false;
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

    private static boolean canExtract(@NotNull Inventory extractorInventory, 
                                      @NotNull Inventory fromInventory, 
                                      @NotNull ItemStack stack, 
                                      int slot, 
                                      @NotNull Direction facing) {
        if (!fromInventory.canTransferTo(extractorInventory, slot, stack)) {
            return false;
        }
        return !(fromInventory instanceof SidedInventory sidedInventory) || sidedInventory.canExtract(slot, stack, facing);
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
                to.setStack(slot, stack);
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
                                                                @NotNull BlockState state) {
        var inputDirection = state.get(ItemExtractorBlock.FACING).getOpposite();
        var inventories = new HashMap<Direction, Inventory>();
        for (var outputDirection : Direction.values()) {
            if(outputDirection == inputDirection) {
                continue;
            }
            var inventory = getInventoryAt(world, pos.offset(outputDirection));
            if(inventory != null && !(inventory instanceof ItemExtractorBlockEntity)) {
                inventories.put(outputDirection, inventory);
            }
        }
        return inventories;
    }

    @Nullable
    private static Inventory getInputInventory(@NotNull World world,
                                               @NotNull ItemExtractorBlockEntity extractor,
                                               @NotNull Direction direction) {
        var pos = extractor.getPos();
        return getInventoryAt(world, pos.offset(direction));
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

    @NotNull
    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        return this.inventory;
    }

    @Override
    protected void setInvStackList(@NotNull DefaultedList<ItemStack> list) {
        this.inventory = list;
    }

    @NotNull
    @Override
    protected ScreenHandler createScreenHandler(int syncId,
                                                @NotNull PlayerInventory playerInventory) {
        return new GenericContainerScreenHandler(Screens.GENERIC_9X1, syncId, playerInventory, this, 1);
    }

    @Override
    public void writeScreenOpeningData(@NotNull ServerPlayerEntity player,
                                       @NotNull PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }
}
