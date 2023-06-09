package com.github.sib_energy_craft.pipes.item_extractor.load;

import com.github.sib_energy_craft.pipes.item_extractor.block.entity.BronzeItemExtractorBlockEntity;
import com.github.sib_energy_craft.pipes.item_extractor.block.entity.DiamondItemExtractorBlockEntity;
import com.github.sib_energy_craft.pipes.item_extractor.block.entity.IronItemExtractorBlockEntity;
import com.github.sib_energy_craft.pipes.item_extractor.block.entity.StoneItemExtractorBlockEntity;
import com.github.sib_energy_craft.sec_utils.load.DefaultModInitializer;
import net.minecraft.block.entity.BlockEntityType;

import static com.github.sib_energy_craft.sec_utils.utils.EntityUtils.register;


/**
 * @since 0.0.1
 * @author sibmaks
 */
public final class Entities implements DefaultModInitializer {
    public static final BlockEntityType<StoneItemExtractorBlockEntity> STONE_ITEM_EXTRACTOR;
    public static final BlockEntityType<BronzeItemExtractorBlockEntity> BRONZE_ITEM_EXTRACTOR;
    public static final BlockEntityType<IronItemExtractorBlockEntity> IRON_ITEM_EXTRACTOR;
    public static final BlockEntityType<DiamondItemExtractorBlockEntity> DIAMOND_ITEM_EXTRACTOR;

    static {
        STONE_ITEM_EXTRACTOR = register(Blocks.STONE_ITEM_EXTRACTOR,
                (pos, state) -> new StoneItemExtractorBlockEntity(Blocks.STONE_ITEM_EXTRACTOR.entity(), pos, state));
        BRONZE_ITEM_EXTRACTOR = register(Blocks.BRONZE_ITEM_EXTRACTOR,
                (pos, state) -> new BronzeItemExtractorBlockEntity(Blocks.BRONZE_ITEM_EXTRACTOR.entity(), pos, state));
        IRON_ITEM_EXTRACTOR = register(Blocks.IRON_ITEM_EXTRACTOR,
                (pos, state) -> new IronItemExtractorBlockEntity(Blocks.IRON_ITEM_EXTRACTOR.entity(), pos, state));
        DIAMOND_ITEM_EXTRACTOR = register(Blocks.DIAMOND_ITEM_EXTRACTOR,
                (pos, state) -> new DiamondItemExtractorBlockEntity(Blocks.DIAMOND_ITEM_EXTRACTOR.entity(), pos, state));
    }
}
