package com.github.sib_energy_craft.pipes.item_extractor.load;

import com.github.sib_energy_craft.energy_api.utils.Identifiers;
import com.github.sib_energy_craft.pipes.constants.PipeConstants;
import com.github.sib_energy_craft.pipes.item_extractor.block.BronzeItemExtractorBlock;
import com.github.sib_energy_craft.pipes.item_extractor.block.DiamondItemExtractorBlock;
import com.github.sib_energy_craft.pipes.item_extractor.block.IronItemExtractorBlock;
import com.github.sib_energy_craft.pipes.item_extractor.block.StoneItemExtractorBlock;
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
    public static final Identified<StoneItemExtractorBlock> STONE_ITEM_EXTRACTOR;
    public static final Identified<BronzeItemExtractorBlock> BRONZE_ITEM_EXTRACTOR;
    public static final Identified<IronItemExtractorBlock> IRON_ITEM_EXTRACTOR;
    public static final Identified<DiamondItemExtractorBlock> DIAMOND_ITEM_EXTRACTOR;

    static {
        var stoneSettings = AbstractBlock.Settings
                .of(Material.STONE)
                .strength(1.5F)
                .sounds(BlockSoundGroup.STONE);

        var stoneItemExtractor = new StoneItemExtractorBlock(stoneSettings, PipeConstants.STONE, PipeConstants.STONE);
        STONE_ITEM_EXTRACTOR = register(Identifiers.of("stone_item_extractor"), stoneItemExtractor);

        var bronzeSettings = AbstractBlock.Settings
                .of(Material.METAL)
                .strength(1.75F)
                .sounds(BlockSoundGroup.METAL);

        var bronzeItemExtractor = new BronzeItemExtractorBlock(bronzeSettings, PipeConstants.BRONZE, PipeConstants.BRONZE);
        BRONZE_ITEM_EXTRACTOR = register(Identifiers.of("bronze_item_extractor"), bronzeItemExtractor);

        var ironSettings = AbstractBlock.Settings
                .of(Material.METAL)
                .strength(2F)
                .sounds(BlockSoundGroup.METAL);

        var ironItemExtractor = new IronItemExtractorBlock(ironSettings, PipeConstants.IRON, PipeConstants.IRON);
        IRON_ITEM_EXTRACTOR = register(Identifiers.of("iron_item_extractor"), ironItemExtractor);

        var diamondMaterial = new Material.Builder(MapColor.DIAMOND_BLUE)
                .build();

        var diamondSettings = AbstractBlock.Settings
                .of(diamondMaterial)
                .strength(2.25F)
                .sounds(BlockSoundGroup.METAL);

        var diamondItemExtractor = new DiamondItemExtractorBlock(diamondSettings, PipeConstants.DIAMOND, PipeConstants.DIAMOND);
        DIAMOND_ITEM_EXTRACTOR = register(Identifiers.of("diamond_item_extractor"), diamondItemExtractor);

    }
}
