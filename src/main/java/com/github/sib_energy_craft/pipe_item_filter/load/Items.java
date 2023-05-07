package com.github.sib_energy_craft.pipe_item_filter.load;

import com.github.sib_energy_craft.sec_utils.load.ModRegistrar;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;

import static com.github.sib_energy_craft.sec_utils.utils.ItemUtils.registerBlockItem;


/**
 * @since 0.0.1
 * @author sibmaks
 */
public final class Items implements ModRegistrar {
    public static final Item STONE_PIPE;

    static {
        STONE_PIPE = registerBlockItem(ItemGroups.FUNCTIONAL, Blocks.STONE_PIPE_ITEM_FILTER);
    }
}
