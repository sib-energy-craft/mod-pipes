package com.github.sib_energy_craft.pipes.mixin;

import com.github.sib_energy_craft.pipes.api.ItemConsumer;
import com.github.sib_energy_craft.pipes.api.ItemSupplier;
import com.github.sib_energy_craft.pipes.utils.PipeUtils;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @since 0.0.2
 * @author sibmaks
 */
@Mixin(ChestBlockEntity.class)
public class ChestBlockEntityMixin implements ItemConsumer, ItemSupplier {

    @Override
    public boolean canConsume(@NotNull ItemStack itemStack, @NotNull Direction direction) {
        return PipeUtils.hasSpaceFor(getThis(), itemStack);
    }

    @Override
    public @NotNull ItemStack consume(@NotNull ItemStack itemStack, @NotNull Direction direction) {
        return PipeUtils.consume(getThis(), itemStack);
    }

    @Override
    public @NotNull List<ItemStack> canSupply(@NotNull Direction direction) {
        var chestBlockEntity = getThis();
        return IntStream.range(0, chestBlockEntity.size())
                .mapToObj(chestBlockEntity::getStack)
                .filter(it -> !it.isEmpty())
                .map(ItemStack::copy)
                .collect(Collectors.toList());
    }

    @Override
    public boolean supply(@NotNull ItemStack requested, @NotNull Direction direction) {
        return PipeUtils.supply(getThis(), requested);
    }

    @Override
    public void returnStack(@NotNull ItemStack requested, @NotNull Direction direction) {
        PipeUtils.consume(getThis(), requested);
    }

    private ChestBlockEntity getThis() {
        return (ChestBlockEntity)(Object)this;
    }
}
