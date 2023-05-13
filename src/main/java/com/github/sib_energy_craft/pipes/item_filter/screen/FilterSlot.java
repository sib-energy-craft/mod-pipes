package com.github.sib_energy_craft.pipes.item_filter.screen;

import lombok.Getter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.0.1
 * @author sibmaks
 */
@Getter
public class FilterSlot extends Slot {
    private final int inventoryIndex;

    public FilterSlot(@NotNull Inventory inventory,
                      int inventoryIndex,
                      int index,
                      int x,
                      int y) {
        super(inventory, index, x, y);
        this.inventoryIndex = inventoryIndex;
    }

    @NotNull
    public ItemStack getStack() {
        return this.inventory.getStack(inventoryIndex);
    }

    @Override
    public void setStack(@NotNull ItemStack cursorStack) {
        var stackToInsert = new ItemStack(cursorStack.getItem(), 1);
        this.inventory.setStack(inventoryIndex, stackToInsert);
    }

    @Override
    public boolean canInsert(@NotNull ItemStack stack) {
        var stackToInsert = new ItemStack(stack.getItem(), 1);
        this.inventory.setStack(inventoryIndex, stackToInsert);
        return false;
    }

    @Override
    @NotNull
    public ItemStack insertStack(@NotNull ItemStack stack) {
        var stackToInsert = new ItemStack(stack.getItem(), 1);
        this.inventory.setStack(inventoryIndex, stackToInsert);
        return stack;
    }

    @Override
    @NotNull
    public ItemStack insertStack(@NotNull ItemStack stack, int count) {
        var stackToInsert = new ItemStack(stack.getItem(), 1);
        this.inventory.setStack(inventoryIndex, stackToInsert);
        return stack;
    }

    @Override
    public boolean canTakeItems(@NotNull PlayerEntity playerEntity) {
        this.inventory.setStack(inventoryIndex, ItemStack.EMPTY);
        return false;
    }

    @Override
    protected void onTake(int amount) {
        this.inventory.setStack(inventoryIndex, ItemStack.EMPTY);
    }

    @Override
    public void onTakeItem(@NotNull PlayerEntity player, @NotNull ItemStack stack) {
        this.inventory.setStack(inventoryIndex, ItemStack.EMPTY);
    }

    @Override
    @NotNull
    public ItemStack takeStack(int amount) {
        this.inventory.setStack(inventoryIndex, ItemStack.EMPTY);
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canTakePartial(@NotNull PlayerEntity player) {
        this.inventory.setStack(inventoryIndex, ItemStack.EMPTY);
        return false;
    }

    @Override
    public void setStackNoCallbacks(@NotNull ItemStack stack) {
        var stackToInsert = new ItemStack(stack.getItem(), 1);
        this.inventory.setStack(inventoryIndex, stackToInsert);
    }
}
