package com.github.sib_energy_craft.pipes.trash_can.load;

import com.github.sib_energy_craft.sec_utils.load.DefaultModInitializer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroups;

import static com.github.sib_energy_craft.sec_utils.utils.ItemUtils.registerBlockItem;


/**
 * @since 0.0.6
 * @author sibmaks
 */
public final class Items implements DefaultModInitializer {
    public static final BlockItem STONE_TRASH_CAN;
    public static final BlockItem BRONZE_TRASH_CAN;
    public static final BlockItem IRON_TRASH_CAN;
    public static final BlockItem DIAMOND_TRASH_CAN;

    static {
        STONE_TRASH_CAN = registerBlockItem(ItemGroups.FUNCTIONAL, Blocks.STONE_TRASH_CAN);
        BRONZE_TRASH_CAN = registerBlockItem(ItemGroups.FUNCTIONAL, Blocks.BRONZE_TRASH_CAN);
        IRON_TRASH_CAN = registerBlockItem(ItemGroups.FUNCTIONAL, Blocks.IRON_TRASH_CAN);
        DIAMOND_TRASH_CAN = registerBlockItem(ItemGroups.FUNCTIONAL, Blocks.DIAMOND_TRASH_CAN);
    }
}
