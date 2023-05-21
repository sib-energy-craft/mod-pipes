package com.github.sib_energy_craft.pipes.load.client;

import com.github.sib_energy_craft.pipes.load.ScreenHandlers;
import com.github.sib_energy_craft.pipes.screen.Container9x1Screen;
import com.github.sib_energy_craft.sec_utils.load.DefaultClientModInitializer;
import com.github.sib_energy_craft.sec_utils.utils.ScreenUtils;

/**
 * @since 0.0.6
 * @author sibmaks
 */
public final class Screens implements DefaultClientModInitializer {

    static {
        ScreenUtils.registerScreen(ScreenHandlers.GENERIC_9X1, Container9x1Screen::new);
    }

}
