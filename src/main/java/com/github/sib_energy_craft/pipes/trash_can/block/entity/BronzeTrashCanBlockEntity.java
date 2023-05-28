package com.github.sib_energy_craft.pipes.trash_can.block.entity;

import com.github.sib_energy_craft.pipes.trash_can.block.BronzeTrashCanBlock;
import com.github.sib_energy_craft.pipes.trash_can.load.Entities;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.0.13
 * @author sibmaks
 */
public class BronzeTrashCanBlockEntity extends TrashCanBlockEntity<BronzeTrashCanBlock> {
    public BronzeTrashCanBlockEntity(@NotNull BronzeTrashCanBlock block,
                                     @NotNull BlockPos pos,
                                     @NotNull BlockState state) {
        super(Entities.BRONZE_TRASH_CAN, block, pos, state);
    }

    @NotNull
    @Override
    protected Text getContainerName() {
        return Text.translatable("container.bronze_trash_can");
    }

}
