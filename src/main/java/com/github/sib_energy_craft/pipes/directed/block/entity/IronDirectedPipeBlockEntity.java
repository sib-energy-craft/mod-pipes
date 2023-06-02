package com.github.sib_energy_craft.pipes.directed.block.entity;

import com.github.sib_energy_craft.pipes.directed.block.IronDirectedPipeBlock;
import com.github.sib_energy_craft.pipes.directed.load.Entities;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

/**
 * @author sibmaks
 * @since 0.0.13
 */
public class IronDirectedPipeBlockEntity extends DirectedPipeBlockEntity<IronDirectedPipeBlock> {

    public IronDirectedPipeBlockEntity(@NotNull IronDirectedPipeBlock block,
                                       @NotNull BlockPos pos,
                                       @NotNull BlockState state) {
        super(Entities.IRON_DIRECTED_PIPE, block, pos, state);
    }
}
