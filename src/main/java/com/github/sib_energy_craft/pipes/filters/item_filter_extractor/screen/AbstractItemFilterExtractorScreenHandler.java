package com.github.sib_energy_craft.pipes.filters.item_filter_extractor.screen;

import com.github.sib_energy_craft.pipes.filters.ItemFilterMode;
import com.github.sib_energy_craft.pipes.filters.item_filter_extractor.block.entity.ItemFilterExtractorBlockEntity;
import com.github.sib_energy_craft.pipes.filters.item_filter_extractor.block.entity.ItemFilterExtractorBlockProperties;
import com.github.sib_energy_craft.pipes.filters.screen.FilterSlot;
import com.github.sib_energy_craft.sec_utils.screen.SlotsScreenHandler;
import com.github.sib_energy_craft.sec_utils.screen.slot.SlotGroupMetaBuilder;
import com.github.sib_energy_craft.sec_utils.screen.slot.SlotGroupsMeta;
import com.github.sib_energy_craft.sec_utils.screen.slot.SlotGroupsMetaBuilder;
import com.github.sib_energy_craft.sec_utils.screen.slot.SlotTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.0.1
 * @author sibmaks
 */
public abstract class AbstractItemFilterExtractorScreenHandler extends SlotsScreenHandler {
    private static final ItemFilterMode[] MODES = ItemFilterMode.values();

    private final PropertyDelegate propertyDelegate;
    protected final SlotGroupsMeta slotGroupsMeta;
    private final ScreenHandlerContext context;

    protected AbstractItemFilterExtractorScreenHandler(@NotNull ScreenHandlerType<?> type,
                                                       int syncId,
                                                       @NotNull PlayerInventory playerInventory) {
        this(type, syncId, playerInventory,
                new SimpleInventory(25),
                new SimpleInventory(9),
                new ArrayPropertyDelegate(1),
                ScreenHandlerContext.EMPTY);
    }

    protected AbstractItemFilterExtractorScreenHandler(@NotNull ScreenHandlerType<?> type,
                                                       int syncId,
                                                       @NotNull PlayerInventory playerInventory,
                                                       @NotNull Inventory filterInventory,
                                                       @NotNull Inventory inventory,
                                                       @NotNull PropertyDelegate propertyDelegate,
                                                       @NotNull ScreenHandlerContext context) {
        super(type, syncId);
        checkSize(filterInventory, 25);
        checkSize(inventory, 9);
        checkDataCount(propertyDelegate, 1);

        this.propertyDelegate = propertyDelegate;
        this.context = context;
        this.slotGroupsMeta = buildSlots(playerInventory, filterInventory, inventory);
        addProperties(propertyDelegate);
    }

    private @NotNull SlotGroupsMeta buildSlots(@NotNull PlayerInventory playerInventory,
                                               @NotNull Inventory filterInventory,
                                               @NotNull Inventory inventory) {
        int globalSlotIndex = 0;
        var slotGroupsBuilder = SlotGroupsMetaBuilder.builder();

        int quickAccessSlots = 9;
        {
            var slotQuickAccessGroupBuilder = SlotGroupMetaBuilder.builder(SlotTypes.QUICK_ACCESS);
            for (int i = 0; i < quickAccessSlots; ++i) {
                slotQuickAccessGroupBuilder.addSlot(globalSlotIndex++, i);
                this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 222));
            }
            var quickAccessSlotGroup = slotQuickAccessGroupBuilder.build();
            slotGroupsBuilder.add(quickAccessSlotGroup);
        }

        {
            var slotPlayerGroupBuilder = SlotGroupMetaBuilder.builder(SlotTypes.PLAYER_INVENTORY);
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 9; ++j) {
                    int index = j + i * 9 + quickAccessSlots;
                    slotPlayerGroupBuilder.addSlot(globalSlotIndex++, index);
                    this.addSlot(new Slot(playerInventory, index, 8 + j * 18, 164 + i * 18));
                }
            }
            var playerSlotGroup = slotPlayerGroupBuilder.build();
            slotGroupsBuilder.add(playerSlotGroup);
        }

        {
            var slotFilterGroupBuilder = SlotGroupMetaBuilder.builder(ItemFilterExtractorSlotTypes.FILTER);
            for (int i = 0; i < filterInventory.size(); ++i) {
                slotFilterGroupBuilder.addSlot(globalSlotIndex++, i);
                this.addSlot(new FilterSlot(filterInventory, i, 44 + (i % 5) * 18, 26 + (i / 5) * 18));
            }
            var filterSlotGroup = slotFilterGroupBuilder.build();
            slotGroupsBuilder.add(filterSlotGroup);
        }

        {
            var slotInventoryGroupBuilder = SlotGroupMetaBuilder.builder(ItemFilterExtractorSlotTypes.INVENTORY);
            for (int i = 0; i < inventory.size(); ++i) {
                slotInventoryGroupBuilder.addSlot(globalSlotIndex++, i);
                this.addSlot(new Slot(inventory, i, 8 + i * 18, 126));
            }
            var inventorySlotGroup = slotInventoryGroupBuilder.build();
            slotGroupsBuilder.add(inventorySlotGroup);
        }

        return slotGroupsBuilder.build();
    }

    @NotNull
    @Override
    public ItemStack quickMove(@NotNull PlayerEntity player, int index) {
        var itemStack = ItemStack.EMPTY;
        var slot = this.slots.get(index);
        if (slot.hasStack()) {
            var slotStack = slot.getStack();
            itemStack = slotStack.copy();
            var slotMeta = this.slotGroupsMeta.getByGlobalSlotIndex(index);
            if(slotMeta != null) {
                var slotType = slotMeta.getSlotType();
                if(slotType == ItemFilterExtractorSlotTypes.FILTER) {
                    return ItemStack.EMPTY;
                }
                if (slotType == ItemFilterExtractorSlotTypes.INVENTORY) {
                    if (!insertItem(slotGroupsMeta, slotStack, SlotTypes.QUICK_ACCESS, SlotTypes.PLAYER_INVENTORY)) {
                        return ItemStack.EMPTY;
                    }
                }
                if (slotType == SlotTypes.QUICK_ACCESS) {
                    if (!insertItem(slotGroupsMeta, slotStack, SlotTypes.PLAYER_INVENTORY)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slotType == SlotTypes.PLAYER_INVENTORY) {
                    if (!insertItem(slotGroupsMeta, slotStack, SlotTypes.QUICK_ACCESS)) {
                        return ItemStack.EMPTY;
                    }
                }
            }
            slot.onQuickTransfer(slotStack, itemStack);
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
