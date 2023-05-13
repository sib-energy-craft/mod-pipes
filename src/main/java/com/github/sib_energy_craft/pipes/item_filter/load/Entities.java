package com.github.sib_energy_craft.pipes.item_filter.load;

import com.github.sib_energy_craft.pipes.item_filter.block.entity.DiamondPipeItemFilterBlockEntity;
import com.github.sib_energy_craft.pipes.item_filter.block.entity.IronPipeItemFilterBlockEntity;
import com.github.sib_energy_craft.pipes.item_filter.block.entity.StonePipeItemFilterBlockEntity;
import com.github.sib_energy_craft.sec_utils.load.DefaultModInitializer;
import com.github.sib_energy_craft.sec_utils.utils.EntityUtils;
import net.minecraft.block.entity.BlockEntityType;


/**
 * @since 0.0.1
 * @author sibmaks
 */
public final class Entities implements DefaultModInitializer {
    public static final BlockEntityType<StonePipeItemFilterBlockEntity> STONE_PIPE_ITEM_FILTER;
    public static final BlockEntityType<IronPipeItemFilterBlockEntity> IRON_PIPE_ITEM_FILTER;
    public static final BlockEntityType<DiamondPipeItemFilterBlockEntity> DIAMOND_PIPE_ITEM_FILTER;

    static {
        STONE_PIPE_ITEM_FILTER = EntityUtils.register(Blocks.STONE_PIPE_ITEM_FILTER,
                (pos, state) -> new StonePipeItemFilterBlockEntity(Blocks.STONE_PIPE_ITEM_FILTER.entity(), pos, state));

        IRON_PIPE_ITEM_FILTER = EntityUtils.register(Blocks.IRON_PIPE_ITEM_FILTER,
                (pos, state) -> new IronPipeItemFilterBlockEntity(Blocks.IRON_PIPE_ITEM_FILTER.entity(), pos, state));

        DIAMOND_PIPE_ITEM_FILTER = EntityUtils.register(Blocks.DIAMOND_PIPE_ITEM_FILTER,
                (pos, state) -> new DiamondPipeItemFilterBlockEntity(Blocks.DIAMOND_PIPE_ITEM_FILTER.entity(), pos, state));
    }
}
