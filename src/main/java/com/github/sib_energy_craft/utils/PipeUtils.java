package com.github.sib_energy_craft.utils;

import com.github.sib_energy_craft.pipes.api.ItemConsumer;
import com.github.sib_energy_craft.pipes.api.ItemSupplier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 0.0.1
 * @author sibmaks
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PipeUtils {

    public static boolean insert(@NotNull World world,
                                 @NotNull BlockPos pos,
                                 @NotNull ItemSupplier itemSupplier,
                                 @NotNull Direction consumedDirection) {
        var itemConsumers = getItemConsumers(world, pos, consumedDirection);
        for (var entry : itemConsumers.entrySet()) {
            var direction = entry.getKey();
            var itemConsumer = entry.getValue();
            var stacksToConsume = itemSupplier.canSupply(direction);
            for (var stackToConsume : stacksToConsume) {
                if(itemConsumer.canConsume(stackToConsume, direction)) {
                    var stackToTransfer = new ItemStack(stackToConsume.getItem(), 1);
                    if(itemSupplier.supply(stackToTransfer, direction)) {
                        var notTransferred = itemConsumer.consume(stackToTransfer, direction);
                        if (notTransferred.isEmpty()) {
                            return true;
                        }
                        itemSupplier.returnStack(stackToConsume, direction);
                    }
                }
            }
        }
        return false;
    }

    /*
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    @NotNull
    public static ItemStack transfer(@NotNull Inventory to,
                                     @NotNull ItemStack stack,
                                     @NotNull Direction side) {
        if (to instanceof SidedInventory sidedInventory) {
            int[] is = sidedInventory.getAvailableSlots(side);
            int i = 0;
            while (i < is.length) {
                if (stack.isEmpty()) return stack;
                stack = transfer(to, stack, is[i], side);
                ++i;
            }
            return stack;
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
    public static ItemStack transfer(@NotNull Inventory to,
                                      @NotNull ItemStack stack,
                                      int slot,
                                      @NotNull Direction side) {
        var itemStack = to.getStack(slot);
        if (canInsert(to, stack, slot, side)) {
            boolean transferred = false;
            if (itemStack.isEmpty()) {
                if(to instanceof ItemConsumer itemConsumer) {
                    itemConsumer.consume(stack, side);
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
    private static Map<Direction, ItemConsumer> getItemConsumers(@NotNull World world,
                                                                 @NotNull BlockPos pos,
                                                                 @NotNull Direction consumedDirection) {
        var inventories = new HashMap<Direction, ItemConsumer>();
        for (var outputDirection : Direction.values()) {
            if(outputDirection == consumedDirection) {
                continue;
            }
            var itemConsumer = getItemConsumer(world, pos.offset(outputDirection));
            if(itemConsumer != null) {
                inventories.put(outputDirection, itemConsumer);
            }
        }
        return inventories;
    }

    @Nullable
    public static ItemConsumer getItemConsumer(@NotNull World world,
                                               @NotNull BlockPos pos) {
        return getItemConsumer(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    @Nullable
    private static ItemConsumer getItemConsumer(@NotNull World world,
                                            double x,
                                            double y,
                                            double z) {
        var blockPos = BlockPos.ofFloored(x, y, z);
        var blockState = world.getBlockState(blockPos);
        if (blockState.hasBlockEntity()) {
            var blockEntity = world.getBlockEntity(blockPos);
            if(blockEntity instanceof ItemConsumer itemConsumer) {
                return itemConsumer;
            }
        }
        return null;
    }

    @Nullable
    public static ItemSupplier getItemSupplier(@NotNull World world,
                                               @NotNull BlockPos pos) {
        return getItemSupplier(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    @Nullable
    private static ItemSupplier getItemSupplier(@NotNull World world,
                                            double x,
                                            double y,
                                            double z) {
        var blockPos = BlockPos.ofFloored(x, y, z);
        var blockState = world.getBlockState(blockPos);
        if (blockState.hasBlockEntity()) {
            var blockEntity = world.getBlockEntity(blockPos);
            if(blockEntity instanceof ItemSupplier itemSupplier) {
                return itemSupplier;
            }
        }
        return null;
    }

    public static boolean canMergeItems(@NotNull ItemStack first,
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

    public static boolean hasSpaceFor(@NotNull Inventory inventory, @NotNull ItemStack itemStack) {
        for(int i = 0; i < inventory.size(); i++) {
            var inventoryStack = inventory.getStack(i);
            if(inventoryStack.isEmpty() || inventoryStack.isItemEqual(itemStack) &&
                    inventoryStack.getCount() < inventoryStack.getMaxCount()) {
                return true;
            }
        }
        return false;
    }


    public static @NotNull ItemStack consume(@NotNull Inventory inventory, @NotNull ItemStack itemStack) {
        ItemStack stackToInsert = null;
        Integer slotToInsert = null;
        for(int i = 0; i < inventory.size(); i++) {
            var inventoryStack = inventory.getStack(i);
            if(inventoryStack.isItemEqual(itemStack) && inventoryStack.getCount() < inventoryStack.getMaxCount()) {
                stackToInsert = inventoryStack;
                break;
            }
            if(slotToInsert == null && inventoryStack.isEmpty()) {
                slotToInsert = i;
            }
        }
        if(stackToInsert == null && slotToInsert == null) {
            return itemStack;
        }
        if(stackToInsert != null) {
            int maxCount = stackToInsert.getMaxCount();
            int sumCount = stackToInsert.getCount() + itemStack.getCount();
            stackToInsert.setCount(Math.min(maxCount, sumCount));
            if (sumCount > maxCount) {
                var lost = new ItemStack(itemStack.getItem(), sumCount - maxCount);
                return consume(inventory, lost);
            }
        } else {
            inventory.setStack(slotToInsert, itemStack);
        }
        inventory.markDirty();
        return ItemStack.EMPTY;
    }

    public static boolean supply(@NotNull Inventory inventory, @NotNull ItemStack requested) {
        var items = new HashMap<Item, Integer>();
        for(int i = 0; i < inventory.size(); i++) {
            var inventoryStack = inventory.getStack(i);
            var inventoryItem = inventoryStack.getItem();
            var count = items.computeIfAbsent(inventoryItem, it -> 0);
            items.put(inventoryItem, count + inventoryStack.getCount());
        }
        var contains = items.get(requested.getItem());
        int requestedCount = requested.getCount();
        if(contains == null || contains == 0 || contains < requestedCount) {
            return false;
        }
        for(int i = 0; i < inventory.size(); i++) {
            var inventoryStack = inventory.getStack(i);
            if(inventoryStack.isItemEqual(requested)) {
                int toRemove = Math.min(inventoryStack.getCount(), requestedCount);
                inventoryStack.decrement(toRemove);
                requestedCount -= toRemove;
                if(requestedCount <= 0) {
                    break;
                }
            }
        }
        inventory.markDirty();
        return true;
    }
}
