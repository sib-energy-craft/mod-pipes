package com.github.sib_energy_craft.pipe_item_filter.load;

import com.github.sib_energy_craft.energy_api.utils.Identifiers;
import com.github.sib_energy_craft.pipe_item_filter.block.StonePipeItemFilterBlock;
import com.github.sib_energy_craft.sec_utils.common.Identified;
import com.github.sib_energy_craft.sec_utils.load.ModRegistrar;
import com.github.sib_energy_craft.sec_utils.utils.BlockUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;


/**
 * @since 0.0.1
 * @author sibmaks
 */
public final class Blocks implements ModRegistrar {

    public static final Identified<StonePipeItemFilterBlock> STONE_PIPE_ITEM_FILTER;

    static {
        var stoneSettings = AbstractBlock.Settings
                .of(Material.STONE)
                .strength(1.5F)
                .sounds(BlockSoundGroup.STONE);

        var stonePipeItemFilter = new StonePipeItemFilterBlock(stoneSettings, 28);
        STONE_PIPE_ITEM_FILTER = BlockUtils.register(Identifiers.of("stone_pipe_item_filter"), stonePipeItemFilter);
    }
}
