package com.github.sib_energy_craft.pipes.filters.item_filter_extractor.block.entity;

import com.github.sib_energy_craft.pipes.filters.item_filter_extractor.block.IronItemFilterExtractorBlock;
import com.github.sib_energy_craft.pipes.filters.item_filter_extractor.load.Entities;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.0.6
 * @author sibmaks
 */
public class IronItemFilterExtractorBlockEntity extends ItemFilterExtractorBlockEntity<IronItemFilterExtractorBlock> {
    public IronItemFilterExtractorBlockEntity(@NotNull IronItemFilterExtractorBlock block,
                                              @NotNull BlockPos pos,
                                              @NotNull BlockState state) {
        super(Entities.IRON_ITEM_FILTER_EXTRACTOR, block, pos, state);
    }

    @NotNull
    @Override
    public Text getDisplayName() {
        return Text.translatable("container.iron_item_filter_extractor");
    }

}
