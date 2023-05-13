package com.github.sib_energy_craft.pipes.item_filter.load;

import com.github.sib_energy_craft.energy_api.utils.Identifiers;
import com.github.sib_energy_craft.pipes.item_filter.block.DiamondPipeItemFilterBlock;
import com.github.sib_energy_craft.pipes.item_filter.block.IronPipeItemFilterBlock;
import com.github.sib_energy_craft.pipes.item_filter.block.StonePipeItemFilterBlock;
import com.github.sib_energy_craft.sec_utils.common.Identified;
import com.github.sib_energy_craft.sec_utils.load.DefaultModInitializer;
import com.github.sib_energy_craft.sec_utils.utils.BlockUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;


/**
 * @since 0.0.1
 * @author sibmaks
 */
public final class Blocks implements DefaultModInitializer {

    public static final Identified<StonePipeItemFilterBlock> STONE_PIPE_ITEM_FILTER;
    public static final Identified<IronPipeItemFilterBlock> IRON_PIPE_ITEM_FILTER;
    public static final Identified<DiamondPipeItemFilterBlock> DIAMOND_PIPE_ITEM_FILTER;

    static {
        var stoneSettings = AbstractBlock.Settings
                .of(Material.STONE)
                .strength(1.5F)
                .sounds(BlockSoundGroup.STONE);

        var stonePipeItemFilter = new StonePipeItemFilterBlock(stoneSettings, 28);
        STONE_PIPE_ITEM_FILTER = BlockUtils.register(Identifiers.of("stone_pipe_item_filter"), stonePipeItemFilter);

        var ironSettings = AbstractBlock.Settings
                .of(Material.METAL)
                .strength(2F)
                .sounds(BlockSoundGroup.METAL);

        var ironPipeItemFilterBlock = new IronPipeItemFilterBlock(ironSettings, 18);
        IRON_PIPE_ITEM_FILTER = BlockUtils.register(Identifiers.of("iron_pipe_item_filter"), ironPipeItemFilterBlock);

        var diamondMaterial = new Material.Builder(MapColor.DIAMOND_BLUE)
                .build();

        var diamondSettings = AbstractBlock.Settings
                .of(diamondMaterial)
                .strength(2.5F)
                .sounds(BlockSoundGroup.METAL);

        var diamondPipe = new DiamondPipeItemFilterBlock(diamondSettings, 8);
        DIAMOND_PIPE_ITEM_FILTER = BlockUtils.register(Identifiers.of("diamond_pipe_item_filter"), diamondPipe);
    }
}
