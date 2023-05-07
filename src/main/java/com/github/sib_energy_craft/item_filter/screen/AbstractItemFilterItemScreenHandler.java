package com.github.sib_energy_craft.item_filter.screen;

import com.github.sib_energy_craft.item_filter.ItemFilterMode;
import com.github.sib_energy_craft.item_filter.item.ItemFilterItem;
import lombok.Getter;
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
public abstract class AbstractItemFilterItemScreenHandler extends ScreenHandler {
    private static final Inventory EMPTY_INVENTORY = new SimpleInventory(0);

    @Getter
    private final ItemStack itemStack;
    private final ItemFilterItem filterItem;
    @Getter
    private final Inventory playerInventory;

    protected AbstractItemFilterItemScreenHandler(@NotNull ScreenHandlerType<?> type,
                                                  int syncId,
                                                  @NotNull PlayerInventory playerInventory,
                                                  @NotNull ItemStack itemStack) {
        super(type, syncId);
        this.itemStack = itemStack;
        this.playerInventory = playerInventory;
        var item = itemStack.getItem();
        if(!(item instanceof ItemFilterItem itemFilterItem)) {
            throw new IllegalArgumentException("Item stack not for item filter");
        }
        this.filterItem = itemFilterItem;

        int slots = 0;
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, slots++, 8 + i * 18, 215));
        }
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, slots++, 8 + j * 18, 157 + i * 18));
            }
        }

        var filterInventory = itemFilterItem.getInventory(itemStack);
        for (int i = 0; i < filterInventory.size(); i++) {
            var filterSlot = new FilterSlot(EMPTY_INVENTORY, itemFilterItem, itemStack, i,
                    slots++, 44 + (i % 5) * 18, 26 + (i / 5) * 18);
            this.addSlot(filterSlot);
        }
    }

    @NotNull
    @Override
    public ItemStack quickMove(@NotNull PlayerEntity player, int index) {
        var itemStack = ItemStack.EMPTY;
        var slot = this.slots.get(index);
        if (slot.hasStack()) {
            var slotStack = slot.getStack();
            itemStack = slotStack.copy();
            if (index >= 0 && index < 9 && !insertItem(slotStack, 9, 36, false)) {
                return ItemStack.EMPTY;
            } else if (index >= 9 && index < 36 && !insertItem(slotStack, 0, 9, false)) {
                return ItemStack.EMPTY;
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

    @Override
    public boolean canUse(@NotNull PlayerEntity player) {
        return true;
    }

    @Override
    public boolean onButtonClick(@NotNull PlayerEntity player, int id) {
        // server side
        var button = Buttons.values()[id];
        return onButtonClick(button);
    }

    public boolean onButtonClick(@NotNull Buttons button) {
        if(button == Buttons.CHANGE_MODE) {
            this.updateToClient();
            filterItem.nextMode(itemStack);
            return true;
        }
        return false;
    }

    @NotNull
    public ItemFilterMode getMode() {
        return filterItem.getMode(itemStack);
    }

}
