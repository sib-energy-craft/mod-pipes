package com.github.sib_energy_craft.pipes.load;

import com.github.sib_energy_craft.pipes.block.entity.DiamondPipeBlockEntity;
import com.github.sib_energy_craft.pipes.block.entity.IronPipeBlockEntity;
import com.github.sib_energy_craft.pipes.block.entity.StonePipeBlockEntity;
import com.github.sib_energy_craft.sec_utils.load.DefaultModInitializer;
import com.github.sib_energy_craft.sec_utils.utils.EntityUtils;
import net.minecraft.block.entity.BlockEntityType;


/**
 * @since 0.0.1
 * @author sibmaks
 */
public final class Entities implements DefaultModInitializer {
    public static final BlockEntityType<StonePipeBlockEntity> STONE_PIPE;
    public static final BlockEntityType<IronPipeBlockEntity> IRON_PIPE;
    public static final BlockEntityType<DiamondPipeBlockEntity> DIAMOND_PIPE;

    static {
        STONE_PIPE = EntityUtils.register(Blocks.STONE_PIPE,
                (pos, state) -> new StonePipeBlockEntity(Blocks.STONE_PIPE.entity(), pos, state));
        IRON_PIPE = EntityUtils.register(Blocks.IRON_PIPE,
                (pos, state) -> new IronPipeBlockEntity(Blocks.IRON_PIPE.entity(), pos, state));
        DIAMOND_PIPE = EntityUtils.register(Blocks.DIAMOND_PIPE,
                (pos, state) -> new DiamondPipeBlockEntity(Blocks.DIAMOND_PIPE.entity(), pos, state));
    }
}
