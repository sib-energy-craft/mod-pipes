package com.github.sib_energy_craft.pipes.filters.item_filter_extractor.load;

import com.github.sib_energy_craft.energy_api.utils.Identifiers;
import com.github.sib_energy_craft.pipes.constants.PipeConstants;
import com.github.sib_energy_craft.pipes.filters.item_filter_extractor.block.DiamondItemFilterExtractorBlock;
import com.github.sib_energy_craft.pipes.filters.item_filter_extractor.block.IronItemFilterExtractorBlock;
import com.github.sib_energy_craft.pipes.filters.item_filter_extractor.block.StoneItemFilterExtractorBlock;
import com.github.sib_energy_craft.sec_utils.common.Identified;
import com.github.sib_energy_craft.sec_utils.load.DefaultModInitializer;
import com.github.sib_energy_craft.sec_utils.utils.BlockUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;


/**
 * @since 0.0.6
 * @author sibmaks
 */
public final class Blocks implements DefaultModInitializer {
    public static final Identified<StoneItemFilterExtractorBlock> STONE_ITEM_FILTER_EXTRACTOR;
    public static final Identified<IronItemFilterExtractorBlock> IRON_ITEM_FILTER_EXTRACTOR;
    public static final Identified<DiamondItemFilterExtractorBlock> DIAMOND_ITEM_FILTER_EXTRACTOR;

    static {
        var stoneSettings = AbstractBlock.Settings
                .of(Material.STONE)
                .strength(1.5F)
                .sounds(BlockSoundGroup.STONE);

        var stoneItemFilterExtractor = new StoneItemFilterExtractorBlock(stoneSettings, PipeConstants.STONE, PipeConstants.STONE);
        STONE_ITEM_FILTER_EXTRACTOR = BlockUtils.register(Identifiers.of("stone_item_filter_extractor"), stoneItemFilterExtractor);

        var ironSettings = AbstractBlock.Settings
                .of(Material.METAL)
                .strength(2F)
                .sounds(BlockSoundGroup.METAL);

        var ironItemFilterExtractor = new IronItemFilterExtractorBlock(ironSettings, PipeConstants.IRON, PipeConstants.IRON);
        IRON_ITEM_FILTER_EXTRACTOR = BlockUtils.register(Identifiers.of("iron_item_filter_extractor"), ironItemFilterExtractor);

        var diamondMaterial = new Material.Builder(MapColor.DIAMOND_BLUE)
                .build();

        var diamondSettings = AbstractBlock.Settings
                .of(diamondMaterial)
                .strength(2F)
                .sounds(BlockSoundGroup.METAL);

        var diamondItemFilterExtractor = new DiamondItemFilterExtractorBlock(diamondSettings, PipeConstants.DIAMOND, PipeConstants.DIAMOND);
        DIAMOND_ITEM_FILTER_EXTRACTOR = BlockUtils.register(Identifiers.of("diamond_item_filter_extractor"), diamondItemFilterExtractor);

    }
}
