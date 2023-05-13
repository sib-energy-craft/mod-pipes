package com.github.sib_energy_craft.pipes.item_filter.block.entity;

import com.github.sib_energy_craft.pipes.item_filter.block.DiamondPipeItemFilterBlock;
import com.github.sib_energy_craft.pipes.item_filter.load.Entities;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.0.1
 * @author sibmaks
 */
public class DiamondPipeItemFilterBlockEntity extends PipeItemFilterBlockEntity<DiamondPipeItemFilterBlock> {

    public DiamondPipeItemFilterBlockEntity(@NotNull DiamondPipeItemFilterBlock block,
                                            @NotNull BlockPos pos,
                                            @NotNull BlockState state) {
        super(Entities.DIAMOND_PIPE_ITEM_FILTER, block, pos, state);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.diamond_pipe_item_filter");
    }
}
