package com.github.sib_energy_craft.item_extractor.block.entity;

import com.github.sib_energy_craft.item_extractor.block.DiamondItemExtractorBlock;
import com.github.sib_energy_craft.item_extractor.load.Entities;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.0.1
 * @author sibmaks
 */
public class DiamondItemExtractorBlockEntity extends ItemExtractorBlockEntity<DiamondItemExtractorBlock> {
    public DiamondItemExtractorBlockEntity(@NotNull DiamondItemExtractorBlock block,
                                           @NotNull BlockPos pos,
                                           @NotNull BlockState state) {
        super(Entities.DIAMOND_ITEM_EXTRACTOR, block, pos, state);
    }

    @NotNull
    @Override
    protected Text getContainerName() {
        return Text.translatable("container.diamond_item_extractor");
    }

}
