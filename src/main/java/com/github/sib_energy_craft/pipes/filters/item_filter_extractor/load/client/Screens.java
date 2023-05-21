package com.github.sib_energy_craft.pipes.filters.item_filter_extractor.load.client;

import com.github.sib_energy_craft.pipes.filters.item_filter_extractor.load.ScreenHandlers;
import com.github.sib_energy_craft.pipes.filters.item_filter_extractor.screen.ItemFilterExtractorScreen;
import com.github.sib_energy_craft.sec_utils.load.DefaultClientModInitializer;

import static com.github.sib_energy_craft.sec_utils.utils.ScreenUtils.registerScreen;

/**
 * @since 0.0.6
 * @author sibmaks
 */
public final class Screens implements DefaultClientModInitializer {

    static {
        registerScreen(ScreenHandlers.ITEM_FILTER_EXTRACTOR, ItemFilterExtractorScreen::new);
    }
}
