package com.github.sib_energy_craft.pipes.directed.block.entity;

import com.github.sib_energy_craft.pipes.directed.block.BronzeDirectedPipeBlock;
import com.github.sib_energy_craft.pipes.directed.load.Entities;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

/**
 * @author sibmaks
 * @since 0.0.14
 */
public class BronzeDirectedPipeBlockEntity extends DirectedPipeBlockEntity<BronzeDirectedPipeBlock> {

    public BronzeDirectedPipeBlockEntity(@NotNull BronzeDirectedPipeBlock block,
                                         @NotNull BlockPos pos,
                                         @NotNull BlockState state) {
        super(Entities.BRONZE_DIRECTED_PIPE, block, pos, state);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.bronze_directed_pipe");
    }
}
