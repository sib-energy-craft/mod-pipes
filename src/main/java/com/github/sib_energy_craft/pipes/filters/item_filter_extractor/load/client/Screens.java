package com.github.sib_energy_craft.pipes.filters.item_filter_extractor.load.client;

import com.github.sib_energy_craft.energy_api.utils.Identifiers;
import com.github.sib_energy_craft.pipes.filters.item_filter_extractor.screen.ItemFilterExtractorScreen;
import com.github.sib_energy_craft.pipes.filters.item_filter_extractor.screen.ItemFilterExtractorScreenHandler;
import com.github.sib_energy_craft.sec_utils.load.DefaultClientModInitializer;
import net.minecraft.screen.ScreenHandlerType;

import static com.github.sib_energy_craft.sec_utils.utils.ScreenUtils.register;

/**
 * @since 0.0.6
 * @author sibmaks
 */
public final class Screens implements DefaultClientModInitializer {
    public static ScreenHandlerType<ItemFilterExtractorScreenHandler> ITEM_FILTER_EXTRACTOR;

    static {
        ITEM_FILTER_EXTRACTOR = register(Identifiers.of("item_filter_extractor"),
                ItemFilterExtractorScreenHandler::new, ItemFilterExtractorScreen::new);
    }
}
