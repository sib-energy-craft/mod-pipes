package com.github.sib_energy_craft.item_extractor.load;

import com.github.sib_energy_craft.energy_api.utils.Identifiers;
import com.github.sib_energy_craft.item_extractor.block.DiamondItemExtractorBlock;
import com.github.sib_energy_craft.item_extractor.block.IronItemExtractorBlock;
import com.github.sib_energy_craft.item_extractor.block.StoneItemExtractorBlock;
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
    public static final Identified<StoneItemExtractorBlock> STONE_ITEM_EXTRACTOR;
    public static final Identified<IronItemExtractorBlock> IRON_ITEM_EXTRACTOR;
    public static final Identified<DiamondItemExtractorBlock> DIAMOND_ITEM_EXTRACTOR;

    static {
        var stoneSettings = AbstractBlock.Settings
                .of(Material.STONE)
                .strength(1.5F)
                .sounds(BlockSoundGroup.STONE);

        var stoneItemExtractor = new StoneItemExtractorBlock(stoneSettings, 28, 28);
        STONE_ITEM_EXTRACTOR = BlockUtils.register(Identifiers.of("stone_item_extractor"), stoneItemExtractor);

        var ironSettings = AbstractBlock.Settings
                .of(Material.METAL)
                .strength(2F)
                .sounds(BlockSoundGroup.METAL);

        var ironItemExtractor = new IronItemExtractorBlock(ironSettings, 18, 18);
        IRON_ITEM_EXTRACTOR = BlockUtils.register(Identifiers.of("iron_item_extractor"), ironItemExtractor);

        var diamondMaterial = new Material.Builder(MapColor.DIAMOND_BLUE)
                .build();

        var diamondSettings = AbstractBlock.Settings
                .of(diamondMaterial)
                .strength(2F)
                .sounds(BlockSoundGroup.METAL);

        var diamondItemExtractor = new DiamondItemExtractorBlock(diamondSettings, 8, 8);
        DIAMOND_ITEM_EXTRACTOR = BlockUtils.register(Identifiers.of("diamond_item_extractor"), diamondItemExtractor);

    }
}
