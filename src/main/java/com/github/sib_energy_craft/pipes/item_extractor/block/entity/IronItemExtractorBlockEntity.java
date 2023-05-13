package com.github.sib_energy_craft.pipes.item_extractor.block.entity;

import com.github.sib_energy_craft.pipes.item_extractor.block.IronItemExtractorBlock;
import com.github.sib_energy_craft.pipes.item_extractor.load.Entities;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.0.1
 * @author sibmaks
 */
public class IronItemExtractorBlockEntity extends ItemExtractorBlockEntity<IronItemExtractorBlock> {
    public IronItemExtractorBlockEntity(@NotNull IronItemExtractorBlock block,
                                        @NotNull BlockPos pos,
                                        @NotNull BlockState state) {
        super(Entities.IRON_ITEM_EXTRACTOR, block, pos, state);
    }

    @NotNull
    @Override
    protected Text getContainerName() {
        return Text.translatable("container.iron_item_extractor");
    }

}
