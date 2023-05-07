package com.github.sib_energy_craft.pipe_item_filter.screen;

import com.github.sib_energy_craft.pipe_item_filter.load.Screens;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.0.1
 * @author sibmaks
 */
public class PipeItemFilterItemScreenHandler extends AbstractPipeItemFilterItemScreenHandler {

    public PipeItemFilterItemScreenHandler(int syncId,
                                   @NotNull PlayerInventory playerInventory,
                                   @NotNull Inventory inventory) {
        super(Screens.PIPE_ITEM_FILTER, syncId, playerInventory, inventory);
    }

    public PipeItemFilterItemScreenHandler(int syncId,
                                   @NotNull PlayerInventory playerInventory,
                                   @NotNull PacketByteBuf packetByteBuf) {
        super(Screens.PIPE_ITEM_FILTER, syncId, playerInventory);
    }
}
