package com.github.sib_energy_craft.item_filter.load;

import com.github.sib_energy_craft.energy_api.utils.Identifiers;
import com.github.sib_energy_craft.item_filter.screen.ItemFilterItemScreen;
import com.github.sib_energy_craft.item_filter.screen.ItemFilterItemScreenHandler;
import com.github.sib_energy_craft.sec_utils.load.ModRegistrar;
import net.minecraft.screen.ScreenHandlerType;

import static com.github.sib_energy_craft.sec_utils.utils.ScreenUtils.register;

/**
 * @since 0.0.1
 * @author sibmaks
 */
public final class Screens implements ModRegistrar {
    public static final ScreenHandlerType<ItemFilterItemScreenHandler> ITEM_FILTER_ITEM;

    static {
        ITEM_FILTER_ITEM = register(Identifiers.of("item_filter"), ItemFilterItemScreenHandler::new,
                ItemFilterItemScreen::new);
    }
}
