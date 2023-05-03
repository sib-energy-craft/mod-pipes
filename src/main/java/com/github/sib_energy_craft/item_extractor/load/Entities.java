package com.github.sib_energy_craft.item_extractor.load;

import com.github.sib_energy_craft.item_extractor.block.entity.IronItemExtractorBlockEntity;
import com.github.sib_energy_craft.item_extractor.block.entity.StoneItemExtractorBlockEntity;
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

    static {
        STONE_ITEM_EXTRACTOR = EntityUtils.register(Blocks.STONE_ITEM_EXTRACTOR,
                (pos, state) -> new StoneItemExtractorBlockEntity(Blocks.STONE_ITEM_EXTRACTOR.entity(), pos, state));
        IRON_ITEM_EXTRACTOR = EntityUtils.register(Blocks.IRON_ITEM_EXTRACTOR,
                (pos, state) -> new IronItemExtractorBlockEntity(Blocks.IRON_ITEM_EXTRACTOR.entity(), pos, state));
    }
}
