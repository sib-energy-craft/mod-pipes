package com.github.sib_energy_craft.item_filter.load;

import com.github.sib_energy_craft.energy_api.utils.Identifiers;
import com.github.sib_energy_craft.item_filter.item.ItemFilterItem;
import com.github.sib_energy_craft.sec_utils.load.ModRegistrar;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.util.Rarity;

import static com.github.sib_energy_craft.sec_utils.utils.ItemUtils.register;


/**
 * @since 0.0.1
 * @author sibmaks
 */
public final class Items implements ModRegistrar {
    public static final Item ITEM_FILTER;

    static {
        var commonSettings = new Item.Settings()
                .rarity(Rarity.COMMON);

        var itemFilter = new ItemFilterItem(commonSettings);
        ITEM_FILTER = register(ItemGroups.FUNCTIONAL, Identifiers.of("item_filter"), itemFilter);
    }
}
