package com.github.sib_energy_craft.pipes.filters.item_filter.block.entity;

import com.github.sib_energy_craft.pipes.filters.item_filter.block.BronzePipeItemFilterBlock;
import com.github.sib_energy_craft.pipes.filters.item_filter.load.Entities;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.0.13
 * @author sibmaks
 */
public class BronzePipeItemFilterBlockEntity extends PipeItemFilterBlockEntity<BronzePipeItemFilterBlock> {

    public BronzePipeItemFilterBlockEntity(@NotNull BronzePipeItemFilterBlock block,
                                           @NotNull BlockPos pos,
                                           @NotNull BlockState state) {
        super(Entities.BRONZE_PIPE_ITEM_FILTER, block, pos, state);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.bronze_pipe_item_filter");
    }
}
