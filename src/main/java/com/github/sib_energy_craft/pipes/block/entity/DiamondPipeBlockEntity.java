package com.github.sib_energy_craft.pipes.block.entity;

import com.github.sib_energy_craft.pipes.block.DiamondPipeBlock;
import com.github.sib_energy_craft.pipes.load.Entities;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.0.1
 * @author sibmaks
 */
public class DiamondPipeBlockEntity extends PipeBlockEntity<DiamondPipeBlock> {

    public DiamondPipeBlockEntity(@NotNull DiamondPipeBlock block,
                                  @NotNull BlockPos pos,
                                  @NotNull BlockState state) {
        super(Entities.DIAMOND_PIPE, block, pos, state);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.diamond_pipe");
    }

}
