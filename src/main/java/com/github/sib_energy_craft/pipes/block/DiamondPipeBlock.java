package com.github.sib_energy_craft.pipes.block;

import com.github.sib_energy_craft.pipes.block.entity.DiamondPipeBlockEntity;
import com.github.sib_energy_craft.pipes.block.entity.PipeBlockEntity;
import com.github.sib_energy_craft.pipes.load.Entities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.0.1
 * @author sibmaks
 */
public class DiamondPipeBlock extends PipeBlock {
    public DiamondPipeBlock(@NotNull Settings settings, int ticksToInsert) {
        super(settings, ticksToInsert);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(@NotNull BlockPos pos,
                                         @NotNull BlockState state) {
        return new DiamondPipeBlockEntity(this, pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull World world,
                                                                  @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        return world.isClient ? null : PipeBlock.checkType(type, Entities.DIAMOND_PIPE, PipeBlockEntity::serverTick);
    }
}
