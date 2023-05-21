package com.github.sib_energy_craft.pipes.filters.item_filter_extractor.load;

import com.github.sib_energy_craft.energy_api.utils.Identifiers;
import com.github.sib_energy_craft.pipes.filters.item_filter_extractor.screen.ItemFilterExtractorScreenHandler;
import com.github.sib_energy_craft.sec_utils.load.DefaultModInitializer;
import net.minecraft.screen.ScreenHandlerType;

import static com.github.sib_energy_craft.sec_utils.utils.ScreenUtils.registerHandler;

/**
 * @since 0.0.10
 * @author sibmaks
 */
public final class ScreenHandlers implements DefaultModInitializer {
    public static ScreenHandlerType<ItemFilterExtractorScreenHandler> ITEM_FILTER_EXTRACTOR;

    static {
        ITEM_FILTER_EXTRACTOR = registerHandler(Identifiers.of("item_filter_extractor"),
                ItemFilterExtractorScreenHandler::new);
    }
}
