package com.github.sib_energy_craft.pipes.load;

import com.github.sib_energy_craft.energy_api.utils.Identifiers;
import com.github.sib_energy_craft.pipes.block.BronzePipeBlock;
import com.github.sib_energy_craft.pipes.block.DiamondPipeBlock;
import com.github.sib_energy_craft.pipes.block.IronPipeBlock;
import com.github.sib_energy_craft.pipes.block.StonePipeBlock;
import com.github.sib_energy_craft.pipes.constants.PipeConstants;
import com.github.sib_energy_craft.sec_utils.common.Identified;
import com.github.sib_energy_craft.sec_utils.load.DefaultModInitializer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.MapColor;
import net.minecraft.sound.BlockSoundGroup;

import static com.github.sib_energy_craft.sec_utils.utils.BlockUtils.register;


/**
 * @since 0.0.1
 * @author sibmaks
 */
public final class Blocks implements DefaultModInitializer {

    public static final Identified<StonePipeBlock> STONE_PIPE;
    public static final Identified<BronzePipeBlock> BRONZE_PIPE;
    public static final Identified<IronPipeBlock> IRON_PIPE;
    public static final Identified<DiamondPipeBlock> DIAMOND_PIPE;

    static {
        var stoneSettings = AbstractBlock.Settings.create()
                .mapColor(MapColor.STONE_GRAY)
                .strength(1.5F)
                .sounds(BlockSoundGroup.STONE);

        var stonePipe = new StonePipeBlock(stoneSettings, PipeConstants.STONE);
        STONE_PIPE = register(Identifiers.of("stone_pipe"), stonePipe);

        var bronzeSettings = AbstractBlock.Settings.create()
                .mapColor(MapColor.IRON_GRAY)
                .strength(1.75F)
                .sounds(BlockSoundGroup.METAL);

        var bronzePipe = new BronzePipeBlock(bronzeSettings, PipeConstants.BRONZE);
        BRONZE_PIPE = register(Identifiers.of("bronze_pipe"), bronzePipe);

        var ironSettings = AbstractBlock.Settings.create()
                .mapColor(MapColor.IRON_GRAY)
                .strength(2F)
                .sounds(BlockSoundGroup.METAL);

        var ironPipe = new IronPipeBlock(ironSettings, PipeConstants.IRON);
        IRON_PIPE = register(Identifiers.of("iron_pipe"), ironPipe);

        var diamondSettings = AbstractBlock.Settings.create()
                .mapColor(MapColor.DIAMOND_BLUE)
                .strength(2.25F)
                .sounds(BlockSoundGroup.METAL);

        var diamondPipe = new DiamondPipeBlock(diamondSettings, PipeConstants.DIAMOND);
        DIAMOND_PIPE = register(Identifiers.of("diamond_pipe"), diamondPipe);
    }
}
