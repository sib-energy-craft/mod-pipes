package com.github.sib_energy_craft.pipes.filters.item_filter_extractor.load;

import com.github.sib_energy_craft.pipes.filters.item_filter_extractor.block.entity.BronzeItemFilterExtractorBlockEntity;
import com.github.sib_energy_craft.pipes.filters.item_filter_extractor.block.entity.DiamondItemFilterExtractorBlockEntity;
import com.github.sib_energy_craft.pipes.filters.item_filter_extractor.block.entity.IronItemFilterExtractorBlockEntity;
import com.github.sib_energy_craft.pipes.filters.item_filter_extractor.block.entity.StoneItemFilterExtractorBlockEntity;
import com.github.sib_energy_craft.sec_utils.load.DefaultModInitializer;
import com.github.sib_energy_craft.sec_utils.utils.EntityUtils;
import net.minecraft.block.entity.BlockEntityType;


/**
 * @since 0.0.6
 * @author sibmaks
 */
public final class Entities implements DefaultModInitializer {
    public static final BlockEntityType<StoneItemFilterExtractorBlockEntity> STONE_ITEM_FILTER_EXTRACTOR;
    public static final BlockEntityType<BronzeItemFilterExtractorBlockEntity> BRONZE_ITEM_FILTER_EXTRACTOR;
    public static final BlockEntityType<IronItemFilterExtractorBlockEntity> IRON_ITEM_FILTER_EXTRACTOR;
    public static final BlockEntityType<DiamondItemFilterExtractorBlockEntity> DIAMOND_ITEM_FILTER_EXTRACTOR;

    static {
        STONE_ITEM_FILTER_EXTRACTOR = EntityUtils.register(Blocks.STONE_ITEM_FILTER_EXTRACTOR,
                (pos, state) -> new StoneItemFilterExtractorBlockEntity(Blocks.STONE_ITEM_FILTER_EXTRACTOR.entity(), pos, state));
        BRONZE_ITEM_FILTER_EXTRACTOR = EntityUtils.register(Blocks.BRONZE_ITEM_FILTER_EXTRACTOR,
                (pos, state) -> new BronzeItemFilterExtractorBlockEntity(Blocks.BRONZE_ITEM_FILTER_EXTRACTOR.entity(), pos, state));
        IRON_ITEM_FILTER_EXTRACTOR = EntityUtils.register(Blocks.IRON_ITEM_FILTER_EXTRACTOR,
                (pos, state) -> new IronItemFilterExtractorBlockEntity(Blocks.IRON_ITEM_FILTER_EXTRACTOR.entity(), pos, state));
        DIAMOND_ITEM_FILTER_EXTRACTOR = EntityUtils.register(Blocks.DIAMOND_ITEM_FILTER_EXTRACTOR,
                (pos, state) -> new DiamondItemFilterExtractorBlockEntity(Blocks.DIAMOND_ITEM_FILTER_EXTRACTOR.entity(), pos, state));
    }
}
