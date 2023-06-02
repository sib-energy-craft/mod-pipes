package com.github.sib_energy_craft.pipes.directed.load;

import com.github.sib_energy_craft.sec_utils.load.DefaultModInitializer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;

import static com.github.sib_energy_craft.sec_utils.utils.ItemUtils.registerBlockItem;


/**
 * @author sibmaks
 * @since 0.0.13
 */
public final class Items implements DefaultModInitializer {
    public static final Item STONE_DIRECTED_PIPE;
    public static final Item BRONZE_DIRECTED_PIPE;
    public static final Item IRON_DIRECTED_PIPE;

    static {
        STONE_DIRECTED_PIPE = registerBlockItem(ItemGroups.FUNCTIONAL, Blocks.STONE_DIRECTED_PIPE);
        BRONZE_DIRECTED_PIPE = registerBlockItem(ItemGroups.FUNCTIONAL, Blocks.BRONZE_DIRECTED_PIPE);
        IRON_DIRECTED_PIPE = registerBlockItem(ItemGroups.FUNCTIONAL, Blocks.IRON_DIRECTED_PIPE);
    }
}
