package com.github.sib_energy_craft.pipes.filters.item_filter_extractor.block.entity;

import com.github.sib_energy_craft.pipes.filters.item_filter_extractor.block.BronzeItemFilterExtractorBlock;
import com.github.sib_energy_craft.pipes.filters.item_filter_extractor.load.Entities;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.0.13
 * @author sibmaks
 */
public class BronzeItemFilterExtractorBlockEntity extends ItemFilterExtractorBlockEntity<BronzeItemFilterExtractorBlock> {
    public BronzeItemFilterExtractorBlockEntity(@NotNull BronzeItemFilterExtractorBlock block,
                                                @NotNull BlockPos pos,
                                                @NotNull BlockState state) {
        super(Entities.BRONZE_ITEM_FILTER_EXTRACTOR, block, pos, state);
    }

    @NotNull
    @Override
    public Text getDisplayName() {
        return Text.translatable("container.bronze_item_filter_extractor");
    }

}
