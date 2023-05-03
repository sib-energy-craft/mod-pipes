package com.github.sib_energy_craft.pipes.tags;

import com.github.sib_energy_craft.energy_api.utils.Identifiers;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.0.1
 * @author sibmaks
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PipeTags {
    private static final TagKey<Block> PIPE_CONSUMER;

    static {
        PIPE_CONSUMER =  TagKey.of(RegistryKeys.BLOCK, Identifiers.of("pipe_consumer"));
    }

    /**
     * Method allow check block stack to ability consume items from pipes.<br/>
     * Validation is performed by item tag, not item type
     *
     * @param blockState block to check
     * @return true - block is energy conductor, false - otherwise
     */
    public static boolean isPipeConsumer(@NotNull BlockState blockState) {
        return blockState.streamTags().anyMatch(it -> it.equals(PIPE_CONSUMER));
    }

}
