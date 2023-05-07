package com.github.sib_energy_craft.item_extractor.block.entity;

import com.github.sib_energy_craft.item_extractor.block.ItemExtractorBlock;
import com.github.sib_energy_craft.item_extractor.load.Screens;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.stream.IntStream;

import static com.github.sib_energy_craft.utils.PipeUtils.getInventoryAt;
import static com.github.sib_energy_craft.utils.PipeUtils.transfer;

/**
 * @since 0.0.1
 * @author sibmaks
 */
public abstract class ItemExtractorBlockEntity<T extends ItemExtractorBlock>
        extends LootableContainerBlockEntity implements ExtendedScreenHandlerFactory {
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(9, ItemStack.EMPTY);

    private final T block;
    private int lastTicksToInsert;
    private int lastTicksToExtract;

    public ItemExtractorBlockEntity(@NotNull BlockEntityType<? extends ItemExtractorBlockEntity<T>> entityType,
                                    @NotNull T block,
                                    @NotNull BlockPos pos,
                                    @NotNull BlockState state) {
        super(entityType, pos, state);
        this.block = block;
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

    public static void serverTick(@NotNull World world,
                                  @NotNull BlockPos pos,
                                  @NotNull BlockState state,
                                  @NotNull ItemExtractorBlockEntity<?> blockEntity) {
        insertAndExtract(world, pos, state, blockEntity, () -> extract(world, blockEntity, state));
    }

    private static void insertAndExtract(@NotNull World world,
                                         @NotNull BlockPos pos,
                                         @NotNull BlockState state,
                                         @NotNull ItemExtractorBlockEntity<?> blockEntity,
                                         @NotNull BooleanSupplier booleanSupplier) {
        if (world.isClient) {
            return;
        }
        boolean modified = false;
        if(!blockEntity.isEmpty() && blockEntity.lastTicksToInsert > 0) {
            blockEntity.lastTicksToInsert--;
        }
        if (!blockEntity.isEmpty() && blockEntity.lastTicksToInsert <= 0) {
            modified = insert(world, pos, state, blockEntity);
            blockEntity.lastTicksToInsert = blockEntity.block.getTicksToInsert();
        }
        if(!blockEntity.isFull() && blockEntity.lastTicksToExtract > 0) {
            blockEntity.lastTicksToExtract--;
        }
        if (!blockEntity.isFull() && blockEntity.lastTicksToExtract <= 0) {
            modified |= booleanSupplier.getAsBoolean();
            blockEntity.lastTicksToExtract = blockEntity.block.getTicksToExtract();
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
                                  @NotNull ItemExtractorBlockEntity<?> extractor,
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

    private static boolean extract(@NotNull ItemExtractorBlockEntity<?> extractor,
                                   @NotNull Inventory inventory,
                                   int slot,
                                   @NotNull Direction side) {
        var itemStack = inventory.getStack(slot);
        if (!itemStack.isEmpty() && canExtract(extractor, inventory, itemStack, slot, side)) {
            var copyStack = itemStack.copy();
            var notTransferred = transfer(extractor, inventory.removeStack(slot, 1), side);
            if (notTransferred.isEmpty()) {
                inventory.markDirty();
                return true;
            }
            inventory.setStack(slot, copyStack);
        }
        return false;
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
                                               @NotNull ItemExtractorBlockEntity<?> extractor,
                                               @NotNull Direction direction) {
        var pos = extractor.getPos();
        return getInventoryAt(world, pos.offset(direction));
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
