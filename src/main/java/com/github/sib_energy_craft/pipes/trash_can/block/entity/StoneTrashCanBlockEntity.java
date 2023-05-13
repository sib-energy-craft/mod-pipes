package com.github.sib_energy_craft.pipes.trash_can.block.entity;

import com.github.sib_energy_craft.pipes.trash_can.block.StoneTrashCanBlock;
import com.github.sib_energy_craft.pipes.trash_can.load.Entities;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.0.6
 * @author sibmaks
 */
public class StoneTrashCanBlockEntity extends TrashCanBlockEntity<StoneTrashCanBlock> {
    public StoneTrashCanBlockEntity(@NotNull StoneTrashCanBlock block,
                                    @NotNull BlockPos pos,
                                    @NotNull BlockState state) {
        super(Entities.STONE_TRASH_CAN, block, pos, state);
    }

    @NotNull
    @Override
    protected Text getContainerName() {
        return Text.translatable("container.stone_trash_can");
    }

}
