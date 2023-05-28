package com.github.sib_energy_craft.pipes.trash_can.load;

import com.github.sib_energy_craft.energy_api.utils.Identifiers;
import com.github.sib_energy_craft.pipes.constants.PipeConstants;
import com.github.sib_energy_craft.pipes.trash_can.block.BronzeTrashCanBlock;
import com.github.sib_energy_craft.pipes.trash_can.block.DiamondTrashCanBlock;
import com.github.sib_energy_craft.pipes.trash_can.block.IronTrashCanBlock;
import com.github.sib_energy_craft.pipes.trash_can.block.StoneTrashCanBlock;
import com.github.sib_energy_craft.sec_utils.common.Identified;
import com.github.sib_energy_craft.sec_utils.load.DefaultModInitializer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;

import static com.github.sib_energy_craft.sec_utils.utils.BlockUtils.register;


/**
 * @since 0.0.6
 * @author sibmaks
 */
public final class Blocks implements DefaultModInitializer {
    public static final Identified<StoneTrashCanBlock> STONE_TRASH_CAN;
    public static final Identified<BronzeTrashCanBlock> BRONZE_TRASH_CAN;
    public static final Identified<IronTrashCanBlock> IRON_TRASH_CAN;
    public static final Identified<DiamondTrashCanBlock> DIAMOND_TRASH_CAN;

    static {
        var stoneSettings = AbstractBlock.Settings
                .of(Material.STONE)
                .strength(1.5F)
                .sounds(BlockSoundGroup.STONE);

        var stoneTrashCan = new StoneTrashCanBlock(stoneSettings, PipeConstants.STONE);
        STONE_TRASH_CAN = register(Identifiers.of("stone_trash_can"), stoneTrashCan);

        var bronzeSettings = AbstractBlock.Settings
                .of(Material.METAL)
                .strength(1.75F)
                .sounds(BlockSoundGroup.METAL);

        var bronzeTrashCan = new BronzeTrashCanBlock(bronzeSettings, PipeConstants.BRONZE);
        BRONZE_TRASH_CAN = register(Identifiers.of("bronze_trash_can"), bronzeTrashCan);

        var ironSettings = AbstractBlock.Settings
                .of(Material.METAL)
                .strength(2F)
                .sounds(BlockSoundGroup.METAL);

        var ironTrashCan = new IronTrashCanBlock(ironSettings, PipeConstants.IRON);
        IRON_TRASH_CAN = register(Identifiers.of("iron_trash_can"), ironTrashCan);

        var diamondMaterial = new Material.Builder(MapColor.DIAMOND_BLUE)
                .build();

        var diamondSettings = AbstractBlock.Settings
                .of(diamondMaterial)
                .strength(2.25F)
                .sounds(BlockSoundGroup.METAL);

        var diamondTrashCan = new DiamondTrashCanBlock(diamondSettings, PipeConstants.DIAMOND);
        DIAMOND_TRASH_CAN = register(Identifiers.of("diamond_trash_can"), diamondTrashCan);

    }
}
