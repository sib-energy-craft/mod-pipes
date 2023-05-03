package com.github.sib_energy_craft.tags;

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
    private static final TagKey<Block> ITEM_EXTRACTOR;

    static {
        PIPE_CONSUMER =  TagKey.of(RegistryKeys.BLOCK, Identifiers.of("pipe_consumer"));
        ITEM_EXTRACTOR =  TagKey.of(RegistryKeys.BLOCK, Identifiers.of("item_extractor"));
    }

    /**
     * Method allow check block stack to ability consume items from pipes.<br/>
     * Validation is performed by item tag, not item type
     *
     * @param blockState block to check
     * @return true - block is item consumer, false - otherwise
     */
    public static boolean isPipeConsumer(@NotNull BlockState blockState) {
        return blockState.streamTags().anyMatch(it -> it.equals(PIPE_CONSUMER));
    }

    /**
     * Method allow check block stack is item extractor or not.<br/>
     * Validation is performed by item tag, not item type
     *
     * @param blockState block to check
     * @return true - block is item extractor, false - otherwise
     */
    public static boolean isItemExtractor(@NotNull BlockState blockState) {
        return blockState.streamTags().anyMatch(it -> it.equals(ITEM_EXTRACTOR));
    }

}
