package com.github.sib_energy_craft.pipes.filters.item_filter.screen;

import com.github.sib_energy_craft.pipes.filters.item_filter.load.ScreenHandlers;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.0.1
 * @author sibmaks
 */
public class PipeItemFilterScreenHandler extends AbstractPipeItemFilterScreenHandler {

    public PipeItemFilterScreenHandler(int syncId,
                                       @NotNull PlayerInventory playerInventory,
                                       @NotNull Inventory inventory,
                                       @NotNull PropertyDelegate propertyDelegate,
                                       @Nullable World world,
                                       @NotNull BlockPos pos) {
        super(ScreenHandlers.PIPE_ITEM_FILTER, syncId, playerInventory, inventory, propertyDelegate,
                ScreenHandlerContext.create(world, pos));
    }

    public PipeItemFilterScreenHandler(int syncId,
                                       @NotNull PlayerInventory playerInventory,
                                       @NotNull PacketByteBuf packetByteBuf) {
        super(ScreenHandlers.PIPE_ITEM_FILTER, syncId, playerInventory);
    }
}
