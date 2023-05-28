package com.github.sib_energy_craft.pipes.item_extractor.block.entity;

import com.github.sib_energy_craft.pipes.item_extractor.block.BronzeItemExtractorBlock;
import com.github.sib_energy_craft.pipes.item_extractor.load.Entities;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.0.13
 * @author sibmaks
 */
public class BronzeItemExtractorBlockEntity extends ItemExtractorBlockEntity<BronzeItemExtractorBlock> {
    public BronzeItemExtractorBlockEntity(@NotNull BronzeItemExtractorBlock block,
                                          @NotNull BlockPos pos,
                                          @NotNull BlockState state) {
        super(Entities.BRONZE_ITEM_EXTRACTOR, block, pos, state);
    }

    @NotNull
    @Override
    public Text getDisplayName() {
        return Text.translatable("container.bronze_item_extractor");
    }

}
