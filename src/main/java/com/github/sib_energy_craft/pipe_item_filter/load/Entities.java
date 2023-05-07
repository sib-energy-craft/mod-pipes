package com.github.sib_energy_craft.pipe_item_filter.load;

import com.github.sib_energy_craft.pipe_item_filter.block.entity.StonePipeItemFilterBlockEntity;
import com.github.sib_energy_craft.sec_utils.load.ModRegistrar;
import com.github.sib_energy_craft.sec_utils.utils.EntityUtils;
import net.minecraft.block.entity.BlockEntityType;


/**
 * @since 0.0.1
 * @author sibmaks
 */
public final class Entities implements ModRegistrar {
    public static final BlockEntityType<StonePipeItemFilterBlockEntity> STONE_PIPE_ITEM_FILTER;

    static {
        STONE_PIPE_ITEM_FILTER = EntityUtils.register(Blocks.STONE_PIPE_ITEM_FILTER,
                (pos, state) -> new StonePipeItemFilterBlockEntity(Blocks.STONE_PIPE_ITEM_FILTER.entity(), pos, state));
    }
}
