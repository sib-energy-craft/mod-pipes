package com.github.sib_energy_craft.pipes.directed.block;

import com.github.sib_energy_craft.pipes.directed.block.entity.DiamondDirectedPipeBlockEntity;
import com.github.sib_energy_craft.pipes.directed.block.entity.DirectedPipeBlockEntity;
import com.github.sib_energy_craft.pipes.directed.load.Entities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author sibmaks
 * @since 0.0.13
 */
public class DiamondDirectedPipeBlock extends DirectedPipeBlock {
    public DiamondDirectedPipeBlock(@NotNull Settings settings,
                                    int ticksToInsert) {
        super(settings, ticksToInsert);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(@NotNull BlockPos pos,
                                         @NotNull BlockState state) {
        return new DiamondDirectedPipeBlockEntity(this, pos, state);
    }

    @Override
    protected BlockEntityType<? extends DirectedPipeBlockEntity<?>> getEntityType() {
        return Entities.DIAMOND_DIRECTED_PIPE;
    }

}
