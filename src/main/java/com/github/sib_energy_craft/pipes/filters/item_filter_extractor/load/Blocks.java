package com.github.sib_energy_craft.pipes.filters.item_filter_extractor.load;

import com.github.sib_energy_craft.energy_api.utils.Identifiers;
import com.github.sib_energy_craft.pipes.constants.PipeConstants;
import com.github.sib_energy_craft.pipes.filters.item_filter_extractor.block.BronzeItemFilterExtractorBlock;
import com.github.sib_energy_craft.pipes.filters.item_filter_extractor.block.DiamondItemFilterExtractorBlock;
import com.github.sib_energy_craft.pipes.filters.item_filter_extractor.block.IronItemFilterExtractorBlock;
import com.github.sib_energy_craft.pipes.filters.item_filter_extractor.block.StoneItemFilterExtractorBlock;
import com.github.sib_energy_craft.sec_utils.common.Identified;
import com.github.sib_energy_craft.sec_utils.load.DefaultModInitializer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.MapColor;
import net.minecraft.sound.BlockSoundGroup;

import static com.github.sib_energy_craft.sec_utils.utils.BlockUtils.register;


/**
 * @since 0.0.6
 * @author sibmaks
 */
public final class Blocks implements DefaultModInitializer {
    public static final Identified<StoneItemFilterExtractorBlock> STONE_ITEM_FILTER_EXTRACTOR;
    public static final Identified<BronzeItemFilterExtractorBlock> BRONZE_ITEM_FILTER_EXTRACTOR;
    public static final Identified<IronItemFilterExtractorBlock> IRON_ITEM_FILTER_EXTRACTOR;
    public static final Identified<DiamondItemFilterExtractorBlock> DIAMOND_ITEM_FILTER_EXTRACTOR;

    static {
        var stoneSettings = AbstractBlock.Settings.create()
                .mapColor(MapColor.STONE_GRAY)
                .strength(1.5F)
                .sounds(BlockSoundGroup.STONE);

        var stoneItemFilterExtractor = new StoneItemFilterExtractorBlock(stoneSettings, PipeConstants.STONE, PipeConstants.STONE);
        STONE_ITEM_FILTER_EXTRACTOR = register(Identifiers.of("stone_item_filter_extractor"), stoneItemFilterExtractor);

        var bronzeSettings = AbstractBlock.Settings.create()
                .mapColor(MapColor.IRON_GRAY)
                .strength(1.75F)
                .sounds(BlockSoundGroup.METAL);

        var bronzeItemFilterExtractor = new BronzeItemFilterExtractorBlock(bronzeSettings, PipeConstants.BRONZE, PipeConstants.BRONZE);
        BRONZE_ITEM_FILTER_EXTRACTOR = register(Identifiers.of("bronze_item_filter_extractor"), bronzeItemFilterExtractor);

        var ironSettings = AbstractBlock.Settings.create()
                .mapColor(MapColor.IRON_GRAY)
                .strength(2F)
                .sounds(BlockSoundGroup.METAL);

        var ironItemFilterExtractor = new IronItemFilterExtractorBlock(ironSettings, PipeConstants.IRON, PipeConstants.IRON);
        IRON_ITEM_FILTER_EXTRACTOR = register(Identifiers.of("iron_item_filter_extractor"), ironItemFilterExtractor);

        var diamondSettings = AbstractBlock.Settings.create()
                .mapColor(MapColor.DIAMOND_BLUE)
                .strength(2.25F)
                .sounds(BlockSoundGroup.METAL);

        var diamondItemFilterExtractor = new DiamondItemFilterExtractorBlock(diamondSettings, PipeConstants.DIAMOND, PipeConstants.DIAMOND);
        DIAMOND_ITEM_FILTER_EXTRACTOR = register(Identifiers.of("diamond_item_filter_extractor"), diamondItemFilterExtractor);

    }
}
