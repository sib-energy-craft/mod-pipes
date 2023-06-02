package com.github.sib_energy_craft.pipes.directed.block.entity;

import com.github.sib_energy_craft.pipes.directed.block.StoneDirectedPipeBlock;
import com.github.sib_energy_craft.pipes.directed.load.Entities;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

/**
 * @author sibmaks
 * @since 0.0.14
 */
public class StoneDirectedPipeBlockEntity extends DirectedPipeBlockEntity<StoneDirectedPipeBlock> {

    public StoneDirectedPipeBlockEntity(@NotNull StoneDirectedPipeBlock block,
                                        @NotNull BlockPos pos,
                                        @NotNull BlockState state) {
        super(Entities.STONE_DIRECTED_PIPE, block, pos, state);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.stone_directed_pipe");
    }
}
