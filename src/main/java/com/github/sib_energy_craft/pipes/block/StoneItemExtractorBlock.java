package com.github.sib_energy_craft.pipes.block;

import com.github.sib_energy_craft.pipes.block.entity.ItemExtractorBlockEntity;
import com.github.sib_energy_craft.pipes.block.entity.StoneItemExtractorBlockEntity;
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
public class StoneItemExtractorBlock extends ItemExtractorBlock {
    public StoneItemExtractorBlock(@NotNull Settings settings, int ticksToExtract, int ticksToInsert) {
        super(settings, ticksToExtract, ticksToInsert);
    }

    @NotNull
    @Override
    public BlockEntity createBlockEntity(@NotNull BlockPos pos,
                                         @NotNull BlockState state) {
        return new StoneItemExtractorBlockEntity(this, pos, state);
    }


    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull World world,
                                                                  @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        return world.isClient ? null : ItemExtractorBlock.checkType(type, Entities.STONE_ITEM_EXTRACTOR,
                ItemExtractorBlockEntity::serverTick);
    }
}
