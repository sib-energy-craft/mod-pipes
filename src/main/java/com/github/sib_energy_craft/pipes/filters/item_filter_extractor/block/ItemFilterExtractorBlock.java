package com.github.sib_energy_craft.pipes.filters.item_filter_extractor.block;

import com.github.sib_energy_craft.pipes.filters.item_filter_extractor.block.entity.ItemFilterExtractorBlockEntity;
import com.github.sib_energy_craft.pipes.filters.item_filter_extractor.load.Stats;
import com.github.sib_energy_craft.pipes.tags.PipeTags;
import lombok.Getter;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @since 0.0.6
 * @author sibmaks
 */
public abstract class ItemFilterExtractorBlock extends ConnectingBlock implements BlockEntityProvider {
    public static final DirectionProperty FACING = Properties.FACING;

    private static final Map<BooleanProperty, Function<BlockPos, BlockPos>> PLACEMENT_MODIFIERS = Map.of(
            NORTH, BlockPos::north,
            SOUTH, BlockPos::south,
            WEST, BlockPos::west,
            EAST, BlockPos::east,
            UP, BlockPos::up,
            DOWN, BlockPos::down
    );

    private static final VoxelShape OUTSIDE_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(2, 0, 2, 14, 1, 14),
            Block.createCuboidShape(3, 1, 3, 13, 2, 13),
            Block.createCuboidShape(5, 2, 5, 11, 3, 11),
            Block.createCuboidShape(6, 3, 6, 10, 10, 10)
    );
    private static final Map<Direction, VoxelShape> VOXEL_SHAPE_MAP = new ConcurrentHashMap<>();

    @Getter
    private final int ticksToExtract;
    @Getter
    private final int ticksToInsert;

    public ItemFilterExtractorBlock(@NotNull AbstractBlock.Settings settings,
                                    int ticksToExtract,
                                    int ticksToInsert) {
        super(0.25f, settings);
        this.ticksToExtract = ticksToExtract;
        this.ticksToInsert = ticksToInsert;
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.UP)
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
    public VoxelShape getOutlineShape(@NotNull BlockState state,
                                      @NotNull BlockView world,
                                      @NotNull BlockPos pos,
                                      @NotNull ShapeContext context) {
        var direction = state.get(FACING);
        return VOXEL_SHAPE_MAP.computeIfAbsent(direction, ItemFilterExtractorBlock::rotateShape);
    }

    @NotNull
    public static VoxelShape rotateShape(@NotNull Direction to) {
        switch (to) {
            case DOWN -> {
                var newShape = VoxelShapes.empty();
                for (var box : OUTSIDE_SHAPE.getBoundingBoxes()) {
                    newShape = VoxelShapes.union(newShape,
                            VoxelShapes.cuboid(box.minX, 1 - box.maxY, box.minZ, box.maxX, 1 - box.minY, box.maxZ));
                }
                return newShape;
            }
            case NORTH -> {
                var newShape = VoxelShapes.empty();
                for (var box : OUTSIDE_SHAPE.getBoundingBoxes()) {
                    newShape = VoxelShapes.union(newShape,
                            VoxelShapes.cuboid(box.minX, box.minZ, 1 - box.maxY, box.maxX, box.maxZ, 1 - box.minY));
                }
                return newShape;
            }
            case SOUTH -> {
                var newShape = VoxelShapes.empty();
                for (var box : OUTSIDE_SHAPE.getBoundingBoxes()) {
                    newShape = VoxelShapes.union(newShape,
                            VoxelShapes.cuboid(box.minX, box.minZ, box.minY, box.maxX, box.maxZ, box.maxY));
                }
                return newShape;
            }
            case WEST -> {
                var newShape = VoxelShapes.empty();
                for (var box : OUTSIDE_SHAPE.getBoundingBoxes()) {
                    newShape = VoxelShapes.union(newShape,
                            VoxelShapes.cuboid(1 - box.maxY, box.minX, box.minZ, 1 - box.minY, box.maxX, box.maxZ));
                }
                return newShape;
            }
            case EAST -> {
                var newShape = VoxelShapes.empty();
                for (var box : OUTSIDE_SHAPE.getBoundingBoxes()) {
                    newShape = VoxelShapes.union(newShape,
                            VoxelShapes.cuboid(box.minY, box.minX, box.minZ, box.maxY, box.maxX, box.maxZ));
                }
                return newShape;
            }
        }
        return OUTSIDE_SHAPE;
    }

    @NotNull
    @Override
    public BlockState getPlacementState(@NotNull ItemPlacementContext ctx) {
        var direction = ctx.getSide();
        var blockView = ctx.getWorld();
        var blockState = this.getDefaultState()
                .with(FACING, direction);
        var blockPos = ctx.getBlockPos();

        for (var entry : FACING_PROPERTIES.entrySet()) {
            var neighborDirection = entry.getKey();
            var sideProperty = entry.getValue();
            var blockPosFunction = PLACEMENT_MODIFIERS.get(sideProperty);
            var neighborPos = blockPosFunction.apply(blockPos);
            var neighborState = blockView.getBlockState(neighborPos);
            var neighborConnected = connectsTo(neighborDirection, blockState, neighborState);
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

    public final boolean connectsTo(@NotNull Direction neighborDirection,
                                    @NotNull BlockState blockState,
                                    @NotNull BlockState neighborState) {
        var direction = blockState.get(FACING).getOpposite();
        return direction != neighborDirection && !cannotConnect(neighborState) && PipeTags.isPipeConsumer(neighborState);
    }

    @Override
    public BlockState getStateForNeighborUpdate(@NotNull BlockState state,
                                                @NotNull Direction direction,
                                                @NotNull BlockState neighborState,
                                                @NotNull WorldAccess world,
                                                @NotNull BlockPos pos,
                                                @NotNull BlockPos neighborPos) {
        return state.with(FACING_PROPERTIES.get(direction), this.connectsTo(direction, state, neighborState));
    }

    @NotNull
    @Override
    public ActionResult onUse(@NotNull BlockState state,
                              @NotNull World world,
                              @NotNull BlockPos pos,
                              @NotNull PlayerEntity player,
                              @NotNull Hand hand,
                              @NotNull BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        var blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ItemFilterExtractorBlockEntity<?> itemFilterExtractorBlockEntity) {
            player.openHandledScreen(itemFilterExtractorBlockEntity);
            player.incrementStat(Stats.INTERACT_ITEM_FILTER_EXTRACTOR);
        }
        return ActionResult.CONSUME;
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
        if (blockEntity instanceof ItemFilterExtractorBlockEntity<?> itemFilterExtractorBlockEntity) {
            ItemScatterer.spawn(world, pos, itemFilterExtractorBlockEntity.getInventory());
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
        state = state.with(FACING, rotation.rotate(state.get(FACING)));
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
        state = state.rotate(mirror.getRotation(state.get(FACING)));
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
        builder.add(FACING, NORTH, EAST, WEST, SOUTH, UP, DOWN);
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