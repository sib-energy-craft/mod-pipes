package com.github.sib_energy_craft.pipes.screen;

import com.github.sib_energy_craft.pipes.load.ScreenHandlers;
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
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.NotNull;

/**
 * @author sibmaks
 * @since 0.0.13
 */
public class OneSlotScreenHandler extends SlotsScreenHandler {
    protected final Inventory inventory;
    protected final SlotGroupsMeta slotGroupsMeta;

    public OneSlotScreenHandler(int syncId,
                                @NotNull PlayerInventory playerInventory,
                                @NotNull PacketByteBuf packetByteBuf) {
        this(syncId, playerInventory, new SimpleInventory(1));
    }

    public OneSlotScreenHandler(int syncId,
                                @NotNull PlayerInventory playerInventory,
                                @NotNull Inventory inventory) {
        super(ScreenHandlers.ONE_SLOT, syncId);
        checkSize(inventory, 1);
        this.inventory = inventory;
        this.slotGroupsMeta = buildSlots(playerInventory, inventory);
    }

    private @NotNull SlotGroupsMeta buildSlots(@NotNull PlayerInventory playerInventory,
                                               @NotNull Inventory inventory) {
        int globalSlotIndex = 0;
        var slotGroupsBuilder = SlotGroupsMetaBuilder.builder();

        int quickAccessSlots = 9;
        {
            var slotQuickAccessGroupBuilder = SlotGroupMetaBuilder.builder(SlotTypes.QUICK_ACCESS);
            for (int i = 0; i < quickAccessSlots; ++i) {
                slotQuickAccessGroupBuilder.addSlot(globalSlotIndex++, i);
                var slot = new Slot(playerInventory, i, 8 + i * 18, 107);
                this.addSlot(slot);
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
                    var slot = new Slot(playerInventory, index, 8 + j * 18, 49 + i * 18);
                    this.addSlot(slot);
                }
            }
            var playerSlotGroup = slotPlayerGroupBuilder.build();
            slotGroupsBuilder.add(playerSlotGroup);
        }

        {
            var slotGroupBuilder = SlotGroupMetaBuilder.builder(ContainerSlotTypes.CONTAINER);
            slotGroupBuilder.addSlot(globalSlotIndex, 0);
            var slot = new Slot(inventory, 0, 80, 18);
            this.addSlot(slot);
            var slotGroup = slotGroupBuilder.build();
            slotGroupsBuilder.add(slotGroup);
        }

        return slotGroupsBuilder.build();
    }

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
                if (slotType == ContainerSlotTypes.CONTAINER) {
                    if (!insertItem(slotGroupsMeta, slotStack, SlotTypes.PLAYER_INVENTORY, SlotTypes.QUICK_ACCESS)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slotType == SlotTypes.QUICK_ACCESS) {
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
        return inventory.canPlayerUse(player);
    }
}
