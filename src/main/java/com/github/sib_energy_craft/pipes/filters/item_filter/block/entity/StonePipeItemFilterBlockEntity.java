package com.github.sib_energy_craft.pipes.filters.item_filter.block.entity;

import com.github.sib_energy_craft.pipes.filters.item_filter.block.StonePipeItemFilterBlock;
import com.github.sib_energy_craft.pipes.filters.item_filter.load.Entities;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.0.1
 * @author sibmaks
 */
public class StonePipeItemFilterBlockEntity extends PipeItemFilterBlockEntity<StonePipeItemFilterBlock> {

    public StonePipeItemFilterBlockEntity(@NotNull StonePipeItemFilterBlock block,
                                          @NotNull BlockPos pos,
                                          @NotNull BlockState state) {
        super(Entities.STONE_PIPE_ITEM_FILTER, block, pos, state);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.stone_pipe_item_filter");
    }
}
