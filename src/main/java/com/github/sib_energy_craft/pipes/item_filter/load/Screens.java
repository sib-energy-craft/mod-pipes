package com.github.sib_energy_craft.pipes.item_filter.load;

import com.github.sib_energy_craft.energy_api.utils.Identifiers;
import com.github.sib_energy_craft.pipes.item_filter.screen.PipeItemFilterScreen;
import com.github.sib_energy_craft.pipes.item_filter.screen.PipeItemFilterScreenHandler;
import com.github.sib_energy_craft.sec_utils.load.DefaultModInitializer;
import net.minecraft.screen.ScreenHandlerType;

import static com.github.sib_energy_craft.sec_utils.utils.ScreenUtils.register;

/**
 * @since 0.0.1
 * @author sibmaks
 */
public final class Screens implements DefaultModInitializer {
    public static ScreenHandlerType<PipeItemFilterScreenHandler> PIPE_ITEM_FILTER;

    static {
        PIPE_ITEM_FILTER = register(Identifiers.of("pipe_item_filter"),
                PipeItemFilterScreenHandler::new, PipeItemFilterScreen::new);
    }
}
