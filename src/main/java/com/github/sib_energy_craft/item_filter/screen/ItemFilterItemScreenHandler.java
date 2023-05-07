package com.github.sib_energy_craft.item_filter.screen;

import com.github.sib_energy_craft.item_filter.load.Screens;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.0.1
 * @author sibmaks
 */
public class ItemFilterItemScreenHandler extends AbstractItemFilterItemScreenHandler {

    public ItemFilterItemScreenHandler(int syncId,
                                       @NotNull ItemStack itemStack,
                                       @NotNull PlayerInventory playerInventory) {
        super(Screens.ITEM_FILTER_ITEM, syncId, playerInventory, itemStack);
    }

    public ItemFilterItemScreenHandler(int syncId,
                                       @NotNull PlayerInventory inventory,
                                       @NotNull PacketByteBuf buf) {
        super(Screens.ITEM_FILTER_ITEM, syncId, inventory, buf.readItemStack());
    }
}
