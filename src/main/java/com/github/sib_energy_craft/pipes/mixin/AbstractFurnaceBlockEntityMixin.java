package com.github.sib_energy_craft.pipes.mixin;

import com.github.sib_energy_craft.pipes.api.ItemConsumer;
import com.github.sib_energy_craft.pipes.api.ItemSupplier;
import com.github.sib_energy_craft.pipes.utils.PipeUtils;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.ItemStack;
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

    @Override
    public boolean canConsume(@NotNull ItemStack itemStack, @NotNull Direction direction) {
        var furnaceBlock = getThis();
        if(AbstractFurnaceBlockEntity.canUseAsFuel(itemStack)) {
            var currentFuel = furnaceBlock.getStack(FUEL_SLOT_INDEX);
            return currentFuel.isEmpty() || PipeUtils.canMergeItems(currentFuel, itemStack);
        }
        var inputStack = furnaceBlock.getStack(INPUT_SLOT_INDEX);
        return getThis().isValid(INPUT_SLOT_INDEX, itemStack) &&
                (inputStack.isEmpty() || PipeUtils.canMergeItems(inputStack, itemStack));
    }

    @Override
    public @NotNull ItemStack consume(@NotNull ItemStack itemStack, @NotNull Direction direction) {
        if(!canConsume(itemStack, direction)) {
            return itemStack;
        }
        var furnaceBlock = getThis();
        if(AbstractFurnaceBlockEntity.canUseAsFuel(itemStack)) {
            var currentFuel = furnaceBlock.getStack(FUEL_SLOT_INDEX);
            if(currentFuel.isEmpty()) {
                furnaceBlock.setStack(FUEL_SLOT_INDEX, itemStack);
                return ItemStack.EMPTY;
            }
            return PipeUtils.mergeItems(currentFuel, itemStack);
        }
        var inputStack = furnaceBlock.getStack(INPUT_SLOT_INDEX);
        if(inputStack.isEmpty()) {
            furnaceBlock.setStack(INPUT_SLOT_INDEX, itemStack);
            return ItemStack.EMPTY;
        }
        return PipeUtils.mergeItems(inputStack, itemStack);
    }

    @Override
    public @NotNull List<ItemStack> canSupply(@NotNull Direction direction) {
        var furnaceBlock = getThis();
        var outputStack = furnaceBlock.getStack(OUTPUT_SLOT_INDEX);
        if(outputStack.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.singletonList(outputStack);
    }

    @Override
    public boolean supply(@NotNull ItemStack requested, @NotNull Direction direction) {
        var furnaceBlock = getThis();
        var outputStack = furnaceBlock.getStack(OUTPUT_SLOT_INDEX);
        if(outputStack.isEmpty() || !ItemStack.areItemsEqual(outputStack, requested) || outputStack.getCount() < requested.getCount()) {
            return false;
        }
        outputStack.decrement(requested.getCount());
        return true;
    }

    @Override
    public void returnStack(@NotNull ItemStack requested, @NotNull Direction direction) {
        var furnaceBlock = getThis();
        var outputStack = furnaceBlock.getStack(OUTPUT_SLOT_INDEX);
        if(outputStack.isEmpty()) {
            furnaceBlock.setStack(OUTPUT_SLOT_INDEX, requested);
        } else {
            outputStack.increment(requested.getCount());
        }
    }

    private AbstractFurnaceBlockEntity getThis() {
        return (AbstractFurnaceBlockEntity)(Object)this;
    }
}
