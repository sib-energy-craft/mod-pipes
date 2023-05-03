package com.github.sib_energy_craft.pipes.load;

import com.github.sib_energy_craft.energy_api.utils.Identifiers;
import com.github.sib_energy_craft.pipes.block.StoneItemExtractorBlock;
import com.github.sib_energy_craft.pipes.block.StonePipeBlock;
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
    public static final Identified<StoneItemExtractorBlock> STONE_ITEM_EXTRACTOR;
    public static final Identified<StonePipeBlock> STONE_PIPE;

    static {
        var stoneSettings = AbstractBlock.Settings
                .of(Material.STONE)
                .strength(2.5F)
                .sounds(BlockSoundGroup.STONE);

        var stoneItemExtractor = new StoneItemExtractorBlock(stoneSettings, 28, 28);
        STONE_ITEM_EXTRACTOR = BlockUtils.register(Identifiers.of("stone_item_extractor"), stoneItemExtractor);

        var stonePipe = new StonePipeBlock(stoneSettings, 28);
        STONE_PIPE = BlockUtils.register(Identifiers.of("stone_pipe"), stonePipe);
    }
}
