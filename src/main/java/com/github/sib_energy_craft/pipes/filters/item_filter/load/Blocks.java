package com.github.sib_energy_craft.pipes.filters.item_filter.load;

import com.github.sib_energy_craft.energy_api.utils.Identifiers;
import com.github.sib_energy_craft.pipes.constants.PipeConstants;
import com.github.sib_energy_craft.pipes.filters.item_filter.block.BronzePipeItemFilterBlock;
import com.github.sib_energy_craft.pipes.filters.item_filter.block.DiamondPipeItemFilterBlock;
import com.github.sib_energy_craft.pipes.filters.item_filter.block.IronPipeItemFilterBlock;
import com.github.sib_energy_craft.pipes.filters.item_filter.block.StonePipeItemFilterBlock;
import com.github.sib_energy_craft.sec_utils.common.Identified;
import com.github.sib_energy_craft.sec_utils.load.DefaultModInitializer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;

import static com.github.sib_energy_craft.sec_utils.utils.BlockUtils.register;


/**
 * @since 0.0.1
 * @author sibmaks
 */
public final class Blocks implements DefaultModInitializer {

    public static final Identified<StonePipeItemFilterBlock> STONE_PIPE_ITEM_FILTER;
    public static final Identified<BronzePipeItemFilterBlock> BRONZE_PIPE_ITEM_FILTER;
    public static final Identified<IronPipeItemFilterBlock> IRON_PIPE_ITEM_FILTER;
    public static final Identified<DiamondPipeItemFilterBlock> DIAMOND_PIPE_ITEM_FILTER;

    static {
        var stoneSettings = AbstractBlock.Settings
                .of(Material.STONE)
                .strength(1.5F)
                .sounds(BlockSoundGroup.STONE);

        var stonePipeItemFilter = new StonePipeItemFilterBlock(stoneSettings, PipeConstants.STONE);
        STONE_PIPE_ITEM_FILTER = register(Identifiers.of("stone_pipe_item_filter"), stonePipeItemFilter);

        var bronzeSettings = AbstractBlock.Settings
                .of(Material.METAL)
                .strength(1.75F)
                .sounds(BlockSoundGroup.METAL);

        var bronzePipeItemFilter = new BronzePipeItemFilterBlock(bronzeSettings, PipeConstants.BRONZE);
        BRONZE_PIPE_ITEM_FILTER = register(Identifiers.of("bronze_pipe_item_filter"), bronzePipeItemFilter);

        var ironSettings = AbstractBlock.Settings
                .of(Material.METAL)
                .strength(2F)
                .sounds(BlockSoundGroup.METAL);

        var ironPipeItemFilterBlock = new IronPipeItemFilterBlock(ironSettings, PipeConstants.IRON);
        IRON_PIPE_ITEM_FILTER = register(Identifiers.of("iron_pipe_item_filter"), ironPipeItemFilterBlock);

        var diamondMaterial = new Material.Builder(MapColor.DIAMOND_BLUE)
                .build();

        var diamondSettings = AbstractBlock.Settings
                .of(diamondMaterial)
                .strength(2.25F)
                .sounds(BlockSoundGroup.METAL);

        var diamondPipe = new DiamondPipeItemFilterBlock(diamondSettings, PipeConstants.DIAMOND);
        DIAMOND_PIPE_ITEM_FILTER = register(Identifiers.of("diamond_pipe_item_filter"), diamondPipe);
    }
}
