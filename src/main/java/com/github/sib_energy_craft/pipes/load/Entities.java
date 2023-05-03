package com.github.sib_energy_craft.pipes.load;

import com.github.sib_energy_craft.pipes.block.StoneItemExtractorBlock;
import com.github.sib_energy_craft.pipes.block.StonePipeBlock;
import com.github.sib_energy_craft.pipes.block.entity.ItemExtractorBlockEntity;
import com.github.sib_energy_craft.pipes.block.entity.PipeBlockEntity;
import com.github.sib_energy_craft.pipes.block.entity.StoneItemExtractorBlockEntity;
import com.github.sib_energy_craft.pipes.block.entity.StonePipeBlockEntity;
import com.github.sib_energy_craft.sec_utils.load.ModRegistrar;
import com.github.sib_energy_craft.sec_utils.utils.EntityUtils;
import net.minecraft.block.entity.BlockEntityType;


/**
 * @since 0.0.1
 * @author sibmaks
 */
public final class Entities implements ModRegistrar {
    public static final BlockEntityType<ItemExtractorBlockEntity<StoneItemExtractorBlock>> STONE_ITEM_EXTRACTOR;
    public static final BlockEntityType<PipeBlockEntity<StonePipeBlock>> STONE_PIPE;

    static {
        STONE_ITEM_EXTRACTOR = EntityUtils.register(Blocks.STONE_ITEM_EXTRACTOR,
                (pos, state) -> new StoneItemExtractorBlockEntity(Blocks.STONE_ITEM_EXTRACTOR.entity(), pos, state));
        STONE_PIPE = EntityUtils.register(Blocks.STONE_PIPE,
                (pos, state) -> new StonePipeBlockEntity(Blocks.STONE_PIPE.entity(), pos, state));
    }
}
