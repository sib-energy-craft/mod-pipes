package com.github.sib_energy_craft.pipes.item_extractor.load;

import com.github.sib_energy_craft.sec_utils.load.DefaultModInitializer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroups;

import static com.github.sib_energy_craft.sec_utils.utils.ItemUtils.registerBlockItem;


/**
 * @since 0.0.1
 * @author sibmaks
 */
public final class Items implements DefaultModInitializer {
    public static final BlockItem STONE_ITEM_EXTRACTOR;
    public static final BlockItem BRONZE_ITEM_EXTRACTOR;
    public static final BlockItem IRON_ITEM_EXTRACTOR;
    public static final BlockItem DIAMOND_ITEM_EXTRACTOR;

    static {
        STONE_ITEM_EXTRACTOR = registerBlockItem(ItemGroups.FUNCTIONAL, Blocks.STONE_ITEM_EXTRACTOR);
        BRONZE_ITEM_EXTRACTOR = registerBlockItem(ItemGroups.FUNCTIONAL, Blocks.BRONZE_ITEM_EXTRACTOR);
        IRON_ITEM_EXTRACTOR = registerBlockItem(ItemGroups.FUNCTIONAL, Blocks.IRON_ITEM_EXTRACTOR);
        DIAMOND_ITEM_EXTRACTOR = registerBlockItem(ItemGroups.FUNCTIONAL, Blocks.DIAMOND_ITEM_EXTRACTOR);
    }
}
