package com.github.sib_energy_craft.item_filter.item;

import com.github.sib_energy_craft.item_filter.screen.ItemFilterItemScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.0.1
 * @author sibmaks
 */
public class ItemFilterItemScreenHandlerFactory implements NamedScreenHandlerFactory, ExtendedScreenHandlerFactory {
    private final World world;
    private final ItemStack itemStack;

    public ItemFilterItemScreenHandlerFactory(@NotNull World world,
                                              @NotNull ItemStack itemStack) {
        this.world = world;
        this.itemStack = itemStack;
    }

    @NotNull
    @Override
    public Text getDisplayName() {
        return Text.translatable("item.sib_energy_craft.item_filter");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId,
                                    @NotNull PlayerInventory playerInventory,
                                    @NotNull PlayerEntity player) {
        return new ItemFilterItemScreenHandler(syncId, itemStack, playerInventory);
    }

    @Override
    public void writeScreenOpeningData(@NotNull ServerPlayerEntity player,
                                       @NotNull PacketByteBuf buf) {
        buf.writeItemStack(itemStack);
    }
}
