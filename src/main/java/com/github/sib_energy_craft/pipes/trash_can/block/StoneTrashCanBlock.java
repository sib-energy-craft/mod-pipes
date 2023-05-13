package com.github.sib_energy_craft.pipes.trash_can.block;

import com.github.sib_energy_craft.pipes.trash_can.block.entity.TrashCanBlockEntity;
import com.github.sib_energy_craft.pipes.trash_can.block.entity.StoneTrashCanBlockEntity;
import com.github.sib_energy_craft.pipes.trash_can.load.Entities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.0.6
 * @author sibmaks
 */
public class StoneTrashCanBlock extends TrashCanBlock {
    public StoneTrashCanBlock(@NotNull Settings settings, int ticksRemove) {
        super(settings, ticksRemove);
    }

    @NotNull
    @Override
    public BlockEntity createBlockEntity(@NotNull BlockPos pos,
                                         @NotNull BlockState state) {
        return new StoneTrashCanBlockEntity(this, pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull World world,
                                                                  @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        return world.isClient ? null : TrashCanBlock.checkType(type, Entities.STONE_TRASH_CAN,
                TrashCanBlockEntity::serverTick);
    }
}
