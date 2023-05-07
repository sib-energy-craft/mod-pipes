package com.github.sib_energy_craft.item_filter.screen;

import com.github.sib_energy_craft.item_filter.item.ItemFilterItem;
import lombok.Getter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.0.1
 * @author sibmaks
 */
@Getter
public class FilterSlot extends Slot {
    private final int inventoryIndex;
    public final ItemFilterItem itemFilterItem;
    public final ItemStack itemStack;

    public FilterSlot(@NotNull Inventory inventory,
                      @NotNull ItemFilterItem itemFilterItem,
                      @NotNull ItemStack itemStack,
                      int inventoryIndex,
                      int index,
                      int x,
                      int y) {
        super(inventory, index, x, y);
        this.inventoryIndex = inventoryIndex;
        this.itemFilterItem = itemFilterItem;
        this.itemStack = itemStack;
    }

    @NotNull
    public ItemStack getStack() {
        var inventory = itemFilterItem.getInventory(itemStack);
        var item = inventory.get(inventoryIndex);
        return new ItemStack(item, 1);
    }

    public void setStack(@NotNull ItemStack cursorStack) {
        itemFilterItem.setItem(itemStack, inventoryIndex, cursorStack.getItem());
    }

    @Override
    public boolean canInsert(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    @NotNull
    public ItemStack insertStack(@NotNull ItemStack stack) {
        itemFilterItem.setItem(itemStack, inventoryIndex, stack.getItem());
        return stack;
    }

    @Override
    @NotNull
    public ItemStack insertStack(@NotNull ItemStack stack, int count) {
        itemFilterItem.setItem(itemStack, inventoryIndex, stack.getItem());
        return stack;
    }

    @Override
    public boolean canTakeItems(@NotNull PlayerEntity playerEntity) {
        return true;
    }

    @Override
    protected void onTake(int amount) {
        itemFilterItem.setItem(itemStack, inventoryIndex, Items.AIR);
    }

    @Override
    public void onTakeItem(@NotNull PlayerEntity player, @NotNull ItemStack stack) {
        itemFilterItem.setItem(itemStack, inventoryIndex, Items.AIR);
    }

    @Override
    @NotNull
    public ItemStack takeStack(int amount) {
        itemFilterItem.setItem(itemStack, inventoryIndex, Items.AIR);
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canTakePartial(@NotNull PlayerEntity player) {
        return false;
    }

    @Override
    public void setStackNoCallbacks(@NotNull ItemStack stack) {
        itemFilterItem.setItem(itemStack, inventoryIndex, stack.getItem());
    }
}
