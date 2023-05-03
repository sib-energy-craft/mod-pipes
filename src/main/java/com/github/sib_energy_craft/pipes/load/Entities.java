package com.github.sib_energy_craft.pipes.load;

import com.github.sib_energy_craft.pipes.block.IronPipeBlock;
import com.github.sib_energy_craft.pipes.block.StonePipeBlock;
import com.github.sib_energy_craft.pipes.block.entity.*;
import com.github.sib_energy_craft.sec_utils.load.ModRegistrar;
import com.github.sib_energy_craft.sec_utils.utils.EntityUtils;
import net.minecraft.block.entity.BlockEntityType;


/**
 * @since 0.0.1
 * @author sibmaks
 */
public final class Entities implements ModRegistrar {
    public static final BlockEntityType<StoneItemExtractorBlockEntity> STONE_ITEM_EXTRACTOR;
    public static final BlockEntityType<IronItemExtractorBlockEntity> IRON_ITEM_EXTRACTOR;

    public static final BlockEntityType<PipeBlockEntity<StonePipeBlock>> STONE_PIPE;
    public static final BlockEntityType<PipeBlockEntity<IronPipeBlock>> IRON_PIPE;

    static {
        STONE_ITEM_EXTRACTOR = EntityUtils.register(Blocks.STONE_ITEM_EXTRACTOR,
                (pos, state) -> new StoneItemExtractorBlockEntity(Blocks.STONE_ITEM_EXTRACTOR.entity(), pos, state));
        IRON_ITEM_EXTRACTOR = EntityUtils.register(Blocks.IRON_ITEM_EXTRACTOR,
                (pos, state) -> new IronItemExtractorBlockEntity(Blocks.IRON_ITEM_EXTRACTOR.entity(), pos, state));
        STONE_PIPE = EntityUtils.register(Blocks.STONE_PIPE,
                (pos, state) -> new StonePipeBlockEntity(Blocks.STONE_PIPE.entity(), pos, state));
        IRON_PIPE = EntityUtils.register(Blocks.IRON_PIPE,
                (pos, state) -> new IronPipeBlockEntity(Blocks.IRON_PIPE.entity(), pos, state));
    }
}
