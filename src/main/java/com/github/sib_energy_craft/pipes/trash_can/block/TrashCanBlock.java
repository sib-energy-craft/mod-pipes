package com.github.sib_energy_craft.pipes.trash_can.block;

import com.github.sib_energy_craft.pipes.trash_can.block.entity.TrashCanBlockEntity;
import com.github.sib_energy_craft.pipes.trash_can.load.Stats;
import lombok.Getter;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.0.6
 * @author sibmaks
 */
public abstract class TrashCanBlock extends BlockWithEntity {
    public static final BooleanProperty OPEN = Properties.OPEN;

    @Getter
    private final int ticksRemove;

    public TrashCanBlock(@NotNull AbstractBlock.Settings settings,
                         int ticksRemove) {
        super(settings);
        this.ticksRemove = ticksRemove;
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(OPEN, false)
        );
    }

    @Override
    public void onPlaced(@NotNull World world,
                         @NotNull BlockPos pos,
                         @NotNull BlockState state,
                         @Nullable LivingEntity placer,
                         @NotNull ItemStack itemStack) {
        if (itemStack.hasCustomName()) {
            var blockEntity = world.getBlockEntity(pos);
            if(blockEntity instanceof TrashCanBlockEntity<?> trashCanBlockEntity) {
                trashCanBlockEntity.setCustomName(itemStack.getName());
            }
        }
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
        if (blockEntity instanceof TrashCanBlockEntity<?> trashCanBlockEntity) {
            player.openHandledScreen(trashCanBlockEntity);
            player.incrementStat(Stats.INTERACT_TRASH_CAN);
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
        if (blockEntity instanceof TrashCanBlockEntity<?> trashCanBlockEntity) {
            ItemScatterer.spawn(world, pos, trashCanBlockEntity);
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

    @Override
    public boolean canPathfindThrough(@NotNull BlockState state,
                                      @NotNull BlockView world,
                                      @NotNull BlockPos pos,
                                      @NotNull NavigationType type) {
        return false;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(OPEN);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        var blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof TrashCanBlockEntity<?> trashCanBlockEntity) {
            trashCanBlockEntity.tick();
        }
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