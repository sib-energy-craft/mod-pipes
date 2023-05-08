package com.github.sib_energy_craft.pipes.block;

import com.github.sib_energy_craft.pipes.block.entity.PipeBlockEntity;
import com.github.sib_energy_craft.tags.PipeTags;
import lombok.Getter;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

/**
 * @since 0.0.1
 * @author sibmaks
 */
public abstract class PipeBlock extends ConnectingBlock implements BlockEntityProvider {
    private static final Map<BooleanProperty, Function<BlockPos, BlockPos>> PLACEMENT_MODIFIERS = Map.of(
            NORTH, BlockPos::north,
            SOUTH, BlockPos::south,
            WEST, BlockPos::west,
            EAST, BlockPos::east,
            UP, BlockPos::up,
            DOWN, BlockPos::down
    );

    @Getter
    private final int ticksToInsert;

    public PipeBlock(@NotNull AbstractBlock.Settings settings,
                     int ticksToInsert) {
        super(0.125f, settings);
        this.ticksToInsert = ticksToInsert;
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(NORTH, false)
                .with(EAST, false)
                .with(SOUTH, false)
                .with(WEST, false)
                .with(UP, false)
                .with(DOWN, false)
        );
    }

    @NotNull
    @Override
    public BlockState getPlacementState(@NotNull ItemPlacementContext ctx) {
        var blockView = ctx.getWorld();
        var blockState = this.getDefaultState();
        var blockPos = ctx.getBlockPos();

        for (var entry : FACING_PROPERTIES.entrySet()) {
            var sideProperty = entry.getValue();
            var blockPosFunction = PLACEMENT_MODIFIERS.get(sideProperty);
            var neighborPos = blockPosFunction.apply(blockPos);
            var neighborState = blockView.getBlockState(neighborPos);
            var neighborConnected = connectsTo(neighborState);
            blockState = blockState.with(sideProperty, neighborConnected);
        }

        return blockState;
    }

    @Override
    public boolean isSideInvisible(@NotNull BlockState state,
                                   @NotNull BlockState stateFrom,
                                   @NotNull Direction direction) {
        if (!stateFrom.isOf(this)) {
            return false;
        }
        return state.get(FACING_PROPERTIES.get(direction)) &&
                stateFrom.get(FACING_PROPERTIES.get(direction.getOpposite()));
    }

    public final boolean connectsTo(@NotNull BlockState neighborState) {
        return !cannotConnect(neighborState) && (PipeTags.isPipeConsumer(neighborState) ||
                PipeTags.isItemExtractor(neighborState));
    }

    @Override
    public BlockState getStateForNeighborUpdate(@NotNull BlockState state,
                                                @NotNull Direction direction,
                                                @NotNull BlockState neighborState,
                                                @NotNull WorldAccess world,
                                                @NotNull BlockPos pos,
                                                @NotNull BlockPos neighborPos) {
        return state.with(FACING_PROPERTIES.get(direction), this.connectsTo(neighborState));
    }

    @Override
    public void onStateReplaced(@NotNull BlockState state,
                                @NotNull World world,
                                @NotNull BlockPos pos,
                                @NotNull BlockState newState,
                                boolean moved) {
        if (state.isOf(newState.getBlock())) {
            return;
        }
        var blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof PipeBlockEntity<?> pipeBlockEntity) {
//            ItemScatterer.spawn(world, pos, pipeBlockEntity);
            world.updateComparators(pos, this);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @NotNull
    @Override
    public BlockRenderType getRenderType(@NotNull BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean hasComparatorOutput(@NotNull BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(@NotNull BlockState state,
                                   @NotNull World world,
                                   @NotNull BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @NotNull
    @Override
    public BlockState rotate(@NotNull BlockState state,
                             @NotNull BlockRotation rotation) {
        switch (rotation) {
            case CLOCKWISE_180 -> {
                return state.with(NORTH, state.get(SOUTH))
                        .with(EAST, state.get(WEST))
                        .with(SOUTH, state.get(NORTH))
                        .with(WEST, state.get(EAST));
            }
            case COUNTERCLOCKWISE_90 -> {
                return state.with(NORTH, state.get(EAST))
                        .with(EAST, state.get(SOUTH))
                        .with(SOUTH, state.get(WEST))
                        .with(WEST, state.get(NORTH));
            }
            case CLOCKWISE_90 -> {
                return state.with(NORTH, state.get(WEST))
                        .with(EAST, state.get(NORTH))
                        .with(SOUTH, state.get(EAST))
                        .with(WEST, state.get(SOUTH));
            }
        }
        return state;
    }

    @NotNull
    @Override
    public BlockState mirror(@NotNull BlockState state,
                             @NotNull BlockMirror mirror) {
        switch (mirror) {
            case LEFT_RIGHT -> {
                return state.with(NORTH, state.get(SOUTH)).with(SOUTH, state.get(NORTH));
            }
            case FRONT_BACK -> {
                return state.with(EAST, state.get(WEST)).with(WEST, state.get(EAST));
            }
        }
        return super.mirror(state, mirror);
    }

    @Override
    protected void appendProperties(@NotNull StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, WEST, SOUTH, UP, DOWN);
    }

    @Override
    public boolean canPathfindThrough(@NotNull BlockState state,
                                      @NotNull BlockView world,
                                      @NotNull BlockPos pos,
                                      @NotNull NavigationType type) {
        return false;
    }

    /**
     *
     *
     * @param givenType given block entity type
     * @param expectedType block entity type to compare
     * @param ticker ticker
     * @return the ticker if the given type and expected type are the same, or {@code null} if they are different
     * @param <E> type of block entity
     * @param <A> type of given block entity
     */
    @Nullable
    protected static <E extends BlockEntity,
            A extends BlockEntity> BlockEntityTicker<A> checkType(@NotNull BlockEntityType<A> givenType,
                                                                  @NotNull BlockEntityType<E> expectedType,
                                                                  @NotNull BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }
}