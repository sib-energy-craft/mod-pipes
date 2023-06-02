package com.github.sib_energy_craft.pipes.directed.load;

import com.github.sib_energy_craft.energy_api.utils.Identifiers;
import com.github.sib_energy_craft.pipes.constants.PipeConstants;
import com.github.sib_energy_craft.pipes.directed.block.BronzeDirectedPipeBlock;
import com.github.sib_energy_craft.pipes.directed.block.StoneDirectedPipeBlock;
import com.github.sib_energy_craft.sec_utils.common.Identified;
import com.github.sib_energy_craft.sec_utils.load.DefaultModInitializer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;

import static com.github.sib_energy_craft.sec_utils.utils.BlockUtils.register;


/**
 * @author sibmaks
 * @since 0.0.13
 */
public final class Blocks implements DefaultModInitializer {

    public static final Identified<StoneDirectedPipeBlock> STONE_DIRECTED_PIPE;
    public static final Identified<BronzeDirectedPipeBlock> BRONZE_DIRECTED_PIPE;

    static {
        var stoneSettings = AbstractBlock.Settings
                .of(Material.STONE)
                .strength(1.5F)
                .sounds(BlockSoundGroup.STONE);

        var stonePipe = new StoneDirectedPipeBlock(stoneSettings, PipeConstants.STONE);
        STONE_DIRECTED_PIPE = register(Identifiers.of("stone_directed_pipe"), stonePipe);

        var bronzeSettings = AbstractBlock.Settings
                .of(Material.METAL)
                .strength(1.75F)
                .sounds(BlockSoundGroup.METAL);

        var bronzeDirectedPipe = new BronzeDirectedPipeBlock(bronzeSettings, PipeConstants.BRONZE);
        BRONZE_DIRECTED_PIPE = register(Identifiers.of("bronze_directed_pipe"), bronzeDirectedPipe);
    }
}
