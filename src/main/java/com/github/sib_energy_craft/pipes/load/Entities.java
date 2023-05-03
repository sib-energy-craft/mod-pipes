package com.github.sib_energy_craft.pipes.load;

import com.github.sib_energy_craft.pipes.block.entity.ItemExtractorBlockEntity;
import com.github.sib_energy_craft.sec_utils.load.ModRegistrar;
import com.github.sib_energy_craft.sec_utils.utils.EntityUtils;
import net.minecraft.block.entity.BlockEntityType;


/**
 * @since 0.0.1
 * @author sibmaks
 */
public final class Entities implements ModRegistrar {
    public static final BlockEntityType<ItemExtractorBlockEntity> ITEM_EXTRACTOR;

    static {
        ITEM_EXTRACTOR = EntityUtils.register(Blocks.ITEM_EXTRACTOR, ItemExtractorBlockEntity::new);
    }
}
