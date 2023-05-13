package com.github.sib_energy_craft.pipes.item_filter.load;

import com.github.sib_energy_craft.energy_api.utils.Identifiers;
import com.github.sib_energy_craft.sec_utils.load.DefaultModInitializer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.util.Rarity;

import static com.github.sib_energy_craft.sec_utils.utils.ItemUtils.register;
import static com.github.sib_energy_craft.sec_utils.utils.ItemUtils.registerBlockItem;


/**
 * @since 0.0.1
 * @author sibmaks
 */
public final class Items implements DefaultModInitializer {
    public static final Item STONE_PIPE_ITEM_FILTER;
    public static final Item IRON_PIPE_ITEM_FILTER;
    public static final Item DIAMOND_PIPE_ITEM_FILTER;
    public static final Item ITEM_FILTER;

    static {
        STONE_PIPE_ITEM_FILTER = registerBlockItem(ItemGroups.FUNCTIONAL, Blocks.STONE_PIPE_ITEM_FILTER);
        IRON_PIPE_ITEM_FILTER = registerBlockItem(ItemGroups.FUNCTIONAL, Blocks.IRON_PIPE_ITEM_FILTER);
        DIAMOND_PIPE_ITEM_FILTER = registerBlockItem(ItemGroups.FUNCTIONAL, Blocks.DIAMOND_PIPE_ITEM_FILTER);

        var commonSettings = new Item.Settings()
                .rarity(Rarity.COMMON);

        var itemFilter = new Item(commonSettings);
        ITEM_FILTER = register(ItemGroups.FUNCTIONAL, Identifiers.of("item_filter"), itemFilter);
    }
}
