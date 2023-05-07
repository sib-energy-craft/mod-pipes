package com.github.sib_energy_craft;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.0.1
 * @author sibmaks
 */
public interface IPipeBlock {

    void setStack(@NotNull Direction direction,
                  @NotNull ItemStack stack);
}
