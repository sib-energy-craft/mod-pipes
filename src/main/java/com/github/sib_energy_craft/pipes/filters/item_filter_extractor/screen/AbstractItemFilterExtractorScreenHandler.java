package com.github.sib_energy_craft.pipes.filters.item_filter_extractor.screen;

import com.github.sib_energy_craft.pipes.filters.ItemFilterMode;
import com.github.sib_energy_craft.pipes.filters.item_filter_extractor.block.entity.ItemFilterExtractorBlockEntity;
import com.github.sib_energy_craft.pipes.filters.item_filter_extractor.block.entity.ItemFilterExtractorBlockProperties;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.0.1
 * @author sibmaks
 */
public abstract class AbstractItemFilterExtractorScreenHandler extends ScreenHandler {
    private static final ItemFilterMode[] MODES = ItemFilterMode.values();

    private final PropertyDelegate propertyDelegate;
    private final ScreenHandlerContext context;

    protected AbstractItemFilterExtractorScreenHandler(@NotNull ScreenHandlerType<?> type,
                                                       int syncId,
                                                       @NotNull PlayerInventory playerInventory) {
        this(type, syncId, playerInventory, new SimpleInventory(25),
                new ArrayPropertyDelegate(1),
                ScreenHandlerContext.EMPTY);
    }

    protected AbstractItemFilterExtractorScreenHandler(@NotNull ScreenHandlerType<?> type,
                                                       int syncId,
                                                       @NotNull PlayerInventory playerInventory,
                                                       @NotNull Inventory filterInventory,
                                                       @NotNull PropertyDelegate propertyDelegate,
                                                       @NotNull ScreenHandlerContext context) {
        super(type, syncId);
        this.propertyDelegate = propertyDelegate;
        this.context = context;
        int slots = 0;
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, slots++, 8 + i * 18, 215));
        }
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, slots++, 8 + j * 18, 157 + i * 18));
            }
        }

        for (int i = 0; i < filterInventory.size(); i++) {
            var filterSlot = new FilterSlot(filterInventory, i, slots++, 44 + (i % 5) * 18, 26 + (i / 5) * 18);
            this.addSlot(filterSlot);
        }
        addProperties(propertyDelegate);
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
            var mode = getMode();
            var nextMode = MODES[(mode.ordinal() + 1) % MODES.length];
            super.setProperty(ItemFilterExtractorBlockProperties.MODE.ordinal(), nextMode.ordinal());
            this.context.run((world, pos) -> {
                var blockEntity = world.getBlockEntity(pos);
                if(blockEntity instanceof ItemFilterExtractorBlockEntity<?> filterBlockEntity) {
                    filterBlockEntity.setMode(nextMode);
                }
            });
            return true;
        }
        return false;
    }

    @NotNull
    public ItemFilterMode getMode() {
        int modeOrdinal = this.propertyDelegate.get(ItemFilterExtractorBlockProperties.MODE.ordinal());
        return MODES[modeOrdinal];
    }

}
