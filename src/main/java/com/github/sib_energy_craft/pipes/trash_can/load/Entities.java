package com.github.sib_energy_craft.pipes.trash_can.load;

import com.github.sib_energy_craft.pipes.trash_can.block.entity.DiamondTrashCanBlockEntity;
import com.github.sib_energy_craft.pipes.trash_can.block.entity.IronTrashCanBlockEntity;
import com.github.sib_energy_craft.pipes.trash_can.block.entity.StoneTrashCanBlockEntity;
import com.github.sib_energy_craft.sec_utils.load.DefaultModInitializer;
import com.github.sib_energy_craft.sec_utils.utils.EntityUtils;
import net.minecraft.block.entity.BlockEntityType;


/**
 * @since 0.0.6
 * @author sibmaks
 */
public final class Entities implements DefaultModInitializer {
    public static final BlockEntityType<StoneTrashCanBlockEntity> STONE_TRASH_CAN;
    public static final BlockEntityType<IronTrashCanBlockEntity> IRON_TRASH_CAN;
    public static final BlockEntityType<DiamondTrashCanBlockEntity> DIAMOND_TRASH_CAN;

    static {
        STONE_TRASH_CAN = EntityUtils.register(Blocks.STONE_TRASH_CAN,
                (pos, state) -> new StoneTrashCanBlockEntity(Blocks.STONE_TRASH_CAN.entity(), pos, state));
        IRON_TRASH_CAN = EntityUtils.register(Blocks.IRON_TRASH_CAN,
                (pos, state) -> new IronTrashCanBlockEntity(Blocks.IRON_TRASH_CAN.entity(), pos, state));
        DIAMOND_TRASH_CAN = EntityUtils.register(Blocks.DIAMOND_TRASH_CAN,
                (pos, state) -> new DiamondTrashCanBlockEntity(Blocks.DIAMOND_TRASH_CAN.entity(), pos, state));
    }
}
