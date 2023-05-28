package com.github.sib_energy_craft.pipes.filters.item_filter_extractor.block;

import com.github.sib_energy_craft.pipes.filters.item_filter_extractor.block.entity.BronzeItemFilterExtractorBlockEntity;
import com.github.sib_energy_craft.pipes.filters.item_filter_extractor.block.entity.ItemFilterExtractorBlockEntity;
import com.github.sib_energy_craft.pipes.filters.item_filter_extractor.load.Entities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.0.13
 * @author sibmaks
 */
public class BronzeItemFilterExtractorBlock extends ItemFilterExtractorBlock {
    public BronzeItemFilterExtractorBlock(@NotNull Settings settings, int ticksToExtract, int ticksToInsert) {
        super(settings, ticksToExtract, ticksToInsert);
    }

    @NotNull
    @Override
    public BlockEntity createBlockEntity(@NotNull BlockPos pos,
                                         @NotNull BlockState state) {
        return new BronzeItemFilterExtractorBlockEntity(this, pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull World world,
                                                                  @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        return world.isClient ? null : ItemFilterExtractorBlock.checkType(type, Entities.BRONZE_ITEM_FILTER_EXTRACTOR,
                ItemFilterExtractorBlockEntity::serverTick);
    }
}
