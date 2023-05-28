package com.github.sib_energy_craft.pipes.filters.item_filter.block;

import com.github.sib_energy_craft.pipes.filters.item_filter.block.entity.DiamondPipeItemFilterBlockEntity;
import com.github.sib_energy_craft.pipes.filters.item_filter.block.entity.PipeItemFilterBlockEntity;
import com.github.sib_energy_craft.pipes.filters.item_filter.load.Entities;
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
public class DiamondPipeItemFilterBlock extends PipeItemFilterBlock {
    public DiamondPipeItemFilterBlock(@NotNull Settings settings, int ticksToInsert) {
        super(settings, ticksToInsert);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(@NotNull BlockPos pos,
                                         @NotNull BlockState state) {
        return new DiamondPipeItemFilterBlockEntity(this, pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull World world,
                                                                  @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        return world.isClient ? null : PipeItemFilterBlock.checkType(type, Entities.DIAMOND_PIPE_ITEM_FILTER,
                PipeItemFilterBlockEntity::serverTick);
    }
}
