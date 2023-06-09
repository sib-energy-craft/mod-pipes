package com.github.sib_energy_craft.pipes.load;

import com.github.sib_energy_craft.sec_utils.load.DefaultModInitializer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;

import static com.github.sib_energy_craft.sec_utils.utils.ItemUtils.registerBlockItem;


/**
 * @since 0.0.1
 * @author sibmaks
 */
public final class Items implements DefaultModInitializer {
    public static final Item STONE_PIPE;
    public static final Item BRONZE_PIPE;
    public static final Item IRON_PIPE;
    public static final Item DIAMOND_PIPE;

    static {
        STONE_PIPE = registerBlockItem(ItemGroups.FUNCTIONAL, Blocks.STONE_PIPE);
        BRONZE_PIPE = registerBlockItem(ItemGroups.FUNCTIONAL, Blocks.BRONZE_PIPE);
        IRON_PIPE = registerBlockItem(ItemGroups.FUNCTIONAL, Blocks.IRON_PIPE);
        DIAMOND_PIPE = registerBlockItem(ItemGroups.FUNCTIONAL, Blocks.DIAMOND_PIPE);
    }
}
