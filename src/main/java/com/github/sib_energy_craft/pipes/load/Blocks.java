package com.github.sib_energy_craft.pipes.load;

import com.github.sib_energy_craft.pipes.block.ItemExtractorBlock;
import com.github.sib_energy_craft.energy_api.utils.Identifiers;
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
    public static final Identified<ItemExtractorBlock> ITEM_EXTRACTOR;

    static {
        var chestSettings = AbstractBlock.Settings
                .of(Material.METAL)
                .strength(2.5F)
                .requiresTool()
                .sounds(BlockSoundGroup.METAL);

        var itemExtractor = new ItemExtractorBlock(chestSettings);
        ITEM_EXTRACTOR = BlockUtils.register(Identifiers.of("item_extractor"), itemExtractor);
    }
}
