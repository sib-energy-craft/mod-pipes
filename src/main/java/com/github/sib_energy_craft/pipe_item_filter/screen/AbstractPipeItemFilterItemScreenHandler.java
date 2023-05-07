package com.github.sib_energy_craft.pipe_item_filter.screen;

import com.github.sib_energy_craft.item_filter.item.ItemFilterItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.0.1
 * @author sibmaks
 */
public abstract class AbstractPipeItemFilterItemScreenHandler extends ScreenHandler {
    private final Inventory inventory;

    protected AbstractPipeItemFilterItemScreenHandler(@NotNull ScreenHandlerType<?> type,
                                                      int syncId,
                                                      @NotNull PlayerInventory playerInventory) {
        this(type, syncId, playerInventory, new SimpleInventory(1));
    }

    protected AbstractPipeItemFilterItemScreenHandler(@NotNull ScreenHandlerType<?> type,
                                                      int syncId,
                                                      @NotNull PlayerInventory playerInventory,
                                                      @NotNull Inventory inventory) {
        super(type, syncId);
        AbstractPipeItemFilterItemScreenHandler.checkSize(inventory, 1);
        this.inventory = inventory;
        this.addSlot(new Slot(inventory, 0, 80, 26));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean canUse(@NotNull PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(@NotNull PlayerEntity player,
                               int index) {
        var itemStack = ItemStack.EMPTY;
        var slot = this.slots.get(index);
        if (slot.hasStack()) {
            var slotStack = slot.getStack();
            itemStack = slotStack.copy();
            if (index == 0) {
                if(!insertItem(slotStack, 1, 37, false)) {
                    return ItemStack.EMPTY;
                }
            } else if(slotStack.getItem() instanceof ItemFilterItem) {
                if(!insertItem(slotStack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if(index >= 1 && index < 27) {
                if(!insertItem(slotStack, 27, 37, false)) {
                    return ItemStack.EMPTY;
                }
            } else if(index >= 27 && index < 37) {
                if(!insertItem(slotStack, 1, 27, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (slotStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
            if (slotStack.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTakeItem(player, slotStack);
        }
        return itemStack;
    }
}
