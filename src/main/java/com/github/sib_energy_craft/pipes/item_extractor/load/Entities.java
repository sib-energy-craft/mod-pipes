package com.github.sib_energy_craft.pipes.item_extractor.load;

import com.github.sib_energy_craft.pipes.item_extractor.block.entity.DiamondItemExtractorBlockEntity;
import com.github.sib_energy_craft.pipes.item_extractor.block.entity.IronItemExtractorBlockEntity;
import com.github.sib_energy_craft.pipes.item_extractor.block.entity.StoneItemExtractorBlockEntity;
import com.github.sib_energy_craft.sec_utils.load.DefaultModInitializer;
import com.github.sib_energy_craft.sec_utils.utils.EntityUtils;
import net.minecraft.block.entity.BlockEntityType;


/**
 * @since 0.0.1
 * @author sibmaks
 */
public final class Entities implements DefaultModInitializer {
    public static final BlockEntityType<StoneItemExtractorBlockEntity> STONE_ITEM_EXTRACTOR;
    public static final BlockEntityType<IronItemExtractorBlockEntity> IRON_ITEM_EXTRACTOR;
    public static final BlockEntityType<DiamondItemExtractorBlockEntity> DIAMOND_ITEM_EXTRACTOR;

    static {
        STONE_ITEM_EXTRACTOR = EntityUtils.register(Blocks.STONE_ITEM_EXTRACTOR,
                (pos, state) -> new StoneItemExtractorBlockEntity(Blocks.STONE_ITEM_EXTRACTOR.entity(), pos, state));
        IRON_ITEM_EXTRACTOR = EntityUtils.register(Blocks.IRON_ITEM_EXTRACTOR,
                (pos, state) -> new IronItemExtractorBlockEntity(Blocks.IRON_ITEM_EXTRACTOR.entity(), pos, state));
        DIAMOND_ITEM_EXTRACTOR = EntityUtils.register(Blocks.DIAMOND_ITEM_EXTRACTOR,
                (pos, state) -> new DiamondItemExtractorBlockEntity(Blocks.DIAMOND_ITEM_EXTRACTOR.entity(), pos, state));
    }
}
