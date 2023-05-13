package com.github.sib_energy_craft.pipes.filters;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @author sibmaks
 * @since 0.0.6
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilterUtils {

    /**
     * Is item fit to filter in specific mode
     *
     * @param filterMode filter mode
     * @param filterInventory filter inventory
     * @param stack stack to check
     * @return true - item fit filter, false - otherwise
     */
    public static boolean isValid(@NotNull ItemFilterMode filterMode,
                                  @NotNull SimpleInventory filterInventory,
                                  @NotNull ItemStack stack) {
        switch (filterMode) {
            case OFF -> {
                return true;
            }
            case WHITELIST -> {
                var stackItem = stack.getItem();
                return filterInventory.stacks.stream().map(ItemStack::getItem).anyMatch(stackItem::equals);
            }
            case BLACKLIST -> {
                var stackItem = stack.getItem();
                return filterInventory.stacks.stream().map(ItemStack::getItem).noneMatch(stackItem::equals);
            }
        }
        return true;
    }

}
