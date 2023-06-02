package com.github.sib_energy_craft.pipes.directed.load;

import com.github.sib_energy_craft.pipes.directed.block.entity.StoneDirectedPipeBlockEntity;
import com.github.sib_energy_craft.sec_utils.load.DefaultModInitializer;
import net.minecraft.block.entity.BlockEntityType;

import static com.github.sib_energy_craft.sec_utils.utils.EntityUtils.register;


/**
 * @author sibmaks
 * @since 0.0.13
 */
public final class Entities implements DefaultModInitializer {
    public static final BlockEntityType<StoneDirectedPipeBlockEntity> STONE_DIRECTED_PIPE;

    static {
        STONE_DIRECTED_PIPE = register(Blocks.STONE_DIRECTED_PIPE,
                (pos, state) -> new StoneDirectedPipeBlockEntity(Blocks.STONE_DIRECTED_PIPE.entity(), pos, state));
    }
}
