package com.github.sib_energy_craft.pipes.mixin;

import com.github.sib_energy_craft.pipes.api.ItemConsumer;
import com.github.sib_energy_craft.pipes.api.ItemSupplier;
import com.github.sib_energy_craft.pipes.utils.PipeUtils;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collections;
import java.util.List;

/**
 * @since 0.0.2
 * @author sibmaks
 */
@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin implements ItemConsumer, ItemSupplier {
    @Shadow
    @Final
    protected static int INPUT_SLOT_INDEX;
    @Shadow
    protected static int FUEL_SLOT_INDEX;
    @Shadow
    protected static int OUTPUT_SLOT_INDEX;
    @Shadow
    private DefaultedList<ItemStack> inventory;

    @Override
    public boolean canConsume(@NotNull ItemStack itemStack, @NotNull Direction direction) {
        if(AbstractFurnaceBlockEntity.canUseAsFuel(itemStack)) {
            var currentFuel = inventory.get(FUEL_SLOT_INDEX);
            return currentFuel.isEmpty() || PipeUtils.canMergeItems(currentFuel, itemStack);
        }
        var inputStack = inventory.get(INPUT_SLOT_INDEX);
        return getThis().isValid(INPUT_SLOT_INDEX, itemStack) &&
                (inputStack.isEmpty() || PipeUtils.canMergeItems(inputStack, itemStack));
    }

    @Override
    public @NotNull ItemStack consume(@NotNull ItemStack itemStack, @NotNull Direction direction) {
        if(!canConsume(itemStack, direction)) {
            return itemStack;
        }
        getThis().markDirty();
        if(AbstractFurnaceBlockEntity.canUseAsFuel(itemStack)) {
            var currentFuel = inventory.get(FUEL_SLOT_INDEX);
            if(currentFuel.isEmpty()) {
                inventory.set(FUEL_SLOT_INDEX, itemStack);
                return ItemStack.EMPTY;
            }
            return PipeUtils.mergeItems(currentFuel, itemStack);
        }
        var inputStack = inventory.get(INPUT_SLOT_INDEX);
        if(inputStack.isEmpty()) {
            inventory.set(INPUT_SLOT_INDEX, itemStack);
            return ItemStack.EMPTY;
        }
        return PipeUtils.mergeItems(inputStack, itemStack);
    }

    @Override
    public @NotNull List<ItemStack> canSupply(@NotNull Direction direction) {
        var outputStack = inventory.get(OUTPUT_SLOT_INDEX);
        if(outputStack.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.singletonList(outputStack);
    }

    @Override
    public boolean supply(@NotNull ItemStack requested, @NotNull Direction direction) {
        var outputStack = inventory.get(OUTPUT_SLOT_INDEX);
        if(outputStack.isEmpty() || !outputStack.isItemEqual(requested) || outputStack.getCount() < requested.getCount()) {
            return false;
        }
        outputStack.decrement(requested.getCount());
        getThis().markDirty();
        return true;
    }

    @Override
    public void returnStack(@NotNull ItemStack requested, @NotNull Direction direction) {
        var outputStack = inventory.get(OUTPUT_SLOT_INDEX);
        if(outputStack.isEmpty()) {
            inventory.set(OUTPUT_SLOT_INDEX, requested);
        } else {
            outputStack.increment(requested.getCount());
        }
        getThis().markDirty();
    }

    private AbstractFurnaceBlockEntity getThis() {
        return (AbstractFurnaceBlockEntity)(Object)this;
    }
}
