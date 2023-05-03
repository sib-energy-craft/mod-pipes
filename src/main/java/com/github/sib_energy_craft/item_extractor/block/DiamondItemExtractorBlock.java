package com.github.sib_energy_craft.item_extractor.block;

import com.github.sib_energy_craft.item_extractor.block.entity.DiamondItemExtractorBlockEntity;
import com.github.sib_energy_craft.item_extractor.load.Entities;
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
public class DiamondItemExtractorBlock extends ItemExtractorBlock {
    public DiamondItemExtractorBlock(@NotNull Settings settings, int ticksToExtract, int ticksToInsert) {
        super(settings, ticksToExtract, ticksToInsert);
    }

    @NotNull
    @Override
    public BlockEntity createBlockEntity(@NotNull BlockPos pos,
                                         @NotNull BlockState state) {
        return new DiamondItemExtractorBlockEntity(this, pos, state);
    }


    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull World world,
                                                                  @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        return world.isClient ? null : ItemExtractorBlock.checkType(type, Entities.DIAMOND_ITEM_EXTRACTOR,
                DiamondItemExtractorBlockEntity::serverTick);
    }
}
