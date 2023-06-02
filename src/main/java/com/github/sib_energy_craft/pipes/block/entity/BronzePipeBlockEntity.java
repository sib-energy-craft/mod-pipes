package com.github.sib_energy_craft.pipes.block.entity;

import com.github.sib_energy_craft.pipes.block.BronzePipeBlock;
import com.github.sib_energy_craft.pipes.load.Entities;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.0.13
 * @author sibmaks
 */
public class BronzePipeBlockEntity extends PipeBlockEntity<BronzePipeBlock> {

    public BronzePipeBlockEntity(@NotNull BronzePipeBlock block,
                                 @NotNull BlockPos pos,
                                 @NotNull BlockState state) {
        super(Entities.BRONZE_PIPE, block, pos, state);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.bronze_pipe");
    }

}
