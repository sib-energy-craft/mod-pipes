package com.github.sib_energy_craft.pipes.filters.item_filter.load.client;

import com.github.sib_energy_craft.pipes.filters.item_filter.load.ScreenHandlers;
import com.github.sib_energy_craft.pipes.filters.item_filter.screen.PipeItemFilterScreen;
import com.github.sib_energy_craft.pipes.filters.item_filter.screen.PipeItemFilterScreenHandler;
import com.github.sib_energy_craft.sec_utils.load.DefaultClientModInitializer;
import net.minecraft.screen.ScreenHandlerType;

import static com.github.sib_energy_craft.sec_utils.utils.ScreenUtils.registerScreen;

/**
 * @since 0.0.1
 * @author sibmaks
 */
public final class Screens implements DefaultClientModInitializer {

    static {
        registerScreen(ScreenHandlers.PIPE_ITEM_FILTER, PipeItemFilterScreen::new);
    }
}
