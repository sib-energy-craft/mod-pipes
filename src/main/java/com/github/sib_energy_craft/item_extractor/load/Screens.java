package com.github.sib_energy_craft.item_extractor.load;

import com.github.sib_energy_craft.energy_api.utils.Identifiers;
import com.github.sib_energy_craft.screen.Container9x1Screen;
import com.github.sib_energy_craft.sec_utils.load.DefaultModInitializer;
import com.github.sib_energy_craft.sec_utils.utils.ScreenUtils;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.0.1
 * @author sibmaks
 */
public final class Screens implements DefaultModInitializer {
    public static final ScreenHandlerType<GenericContainerScreenHandler> GENERIC_9X1;

    static {
        GENERIC_9X1 = ScreenUtils.register(Identifiers.of("generic_9x1"), Screens::createGeneric9x1,
                Container9x1Screen::new);
    }

    public static GenericContainerScreenHandler createGeneric9x1(int syncId,
                                                                 @NotNull PlayerInventory inventory,
                                                                 @NotNull PacketByteBuf buf) {
        int rows = 1;
        var simpleInventory = new SimpleInventory(9 * rows);
        return new GenericContainerScreenHandler(GENERIC_9X1, syncId, inventory, simpleInventory, rows);
    }
}
