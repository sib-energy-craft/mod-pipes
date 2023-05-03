package com.github.sib_energy_craft.pipes.block.entity;

import com.github.sib_energy_craft.pipes.block.StonePipeBlock;
import com.github.sib_energy_craft.pipes.load.Entities;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.0.1
 * @author sibmaks
 */
public class StonePipeBlockEntity extends PipeBlockEntity<StonePipeBlock> {

    public StonePipeBlockEntity(@NotNull StonePipeBlock block,
                                @NotNull BlockPos pos,
                                @NotNull BlockState state) {
        super(Entities.STONE_PIPE, block, pos, state);
    }

}
