package com.github.sib_energy_craft.pipes.filters.item_filter.load;

import com.github.sib_energy_craft.energy_api.utils.Identifiers;
import com.github.sib_energy_craft.pipes.filters.item_filter.screen.PipeItemFilterScreenHandler;
import com.github.sib_energy_craft.sec_utils.load.DefaultModInitializer;
import net.minecraft.screen.ScreenHandlerType;

import static com.github.sib_energy_craft.sec_utils.utils.ScreenUtils.registerHandler;

/**
 * @since 0.0.10
 * @author sibmaks
 */
public final class ScreenHandlers implements DefaultModInitializer {
    public static ScreenHandlerType<PipeItemFilterScreenHandler> PIPE_ITEM_FILTER;

    static {
        PIPE_ITEM_FILTER = registerHandler(Identifiers.of("pipe_item_filter"), PipeItemFilterScreenHandler::new);
    }
}
