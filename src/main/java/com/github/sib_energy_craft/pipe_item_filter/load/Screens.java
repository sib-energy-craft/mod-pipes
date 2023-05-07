package com.github.sib_energy_craft.pipe_item_filter.load;

import com.github.sib_energy_craft.energy_api.utils.Identifiers;
import com.github.sib_energy_craft.pipe_item_filter.screen.PipeItemFilterItemScreen;
import com.github.sib_energy_craft.pipe_item_filter.screen.PipeItemFilterItemScreenHandler;
import com.github.sib_energy_craft.sec_utils.load.ModRegistrar;
import net.minecraft.screen.ScreenHandlerType;

import static com.github.sib_energy_craft.sec_utils.utils.ScreenUtils.register;

/**
 * @since 0.0.1
 * @author sibmaks
 */
public final class Screens implements ModRegistrar {
    public static ScreenHandlerType<PipeItemFilterItemScreenHandler> PIPE_ITEM_FILTER;

    static {
        PIPE_ITEM_FILTER = register(Identifiers.of("pipe_item_filter"),
                PipeItemFilterItemScreenHandler::new, PipeItemFilterItemScreen::new);
    }
}
