package com.github.sib_energy_craft.pipes.filters.item_filter_extractor.block.entity;

import com.github.sib_energy_craft.pipes.filters.item_filter_extractor.block.StoneItemFilterExtractorBlock;
import com.github.sib_energy_craft.pipes.filters.item_filter_extractor.load.Entities;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.0.6
 * @author sibmaks
 */
public class StoneItemFilterExtractorBlockEntity extends ItemFilterExtractorBlockEntity<StoneItemFilterExtractorBlock> {
    public StoneItemFilterExtractorBlockEntity(@NotNull StoneItemFilterExtractorBlock block,
                                               @NotNull BlockPos pos,
                                               @NotNull BlockState state) {
        super(Entities.STONE_ITEM_FILTER_EXTRACTOR, block, pos, state);
    }

    @NotNull
    @Override
    public Text getDisplayName() {
        return Text.translatable("container.stone_item_filter_extractor");
    }

}
