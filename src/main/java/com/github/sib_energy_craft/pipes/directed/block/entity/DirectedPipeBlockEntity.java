package com.github.sib_energy_craft.pipes.directed.block.entity;

import com.github.sib_energy_craft.pipes.api.ItemConsumer;
import com.github.sib_energy_craft.pipes.api.ItemSupplier;
import com.github.sib_energy_craft.pipes.directed.block.DirectedPipeBlock;
import com.github.sib_energy_craft.pipes.utils.PipeUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * @author sibmaks
 * @since 0.0.13
 */
public abstract class DirectedPipeBlockEntity<T extends DirectedPipeBlock> extends BlockEntity
        implements ItemConsumer, ItemSupplier {
    private ItemStack storage;
    private Direction supplyingDirection;

    private final T block;
    private int lastTicksToInsert;

    public DirectedPipeBlockEntity(@NotNull BlockEntityType<? extends DirectedPipeBlockEntity<T>> entityType,
                                   @NotNull T block,
                                   @NotNull BlockPos pos,
                                   @NotNull BlockState state) {
        super(entityType, pos, state);
        this.block = block;
        this.storage = ItemStack.EMPTY;
        this.supplyingDirection = getSupplyingDirection(state);
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);
        storage = ItemStack.fromNbt(nbt.getCompound("storage"));
    }

    @Override
    protected void writeNbt(@NotNull NbtCompound nbt) {
        super.writeNbt(nbt);

        var storageCompound = new NbtCompound();
        storage.writeNbt(storageCompound);
        nbt.put("storage", storageCompound);
    }

    public static void serverTick(@NotNull World world,
                                  @NotNull BlockPos pos,
                                  @NotNull BlockState state,
                                  @NotNull DirectedPipeBlockEntity<?> blockEntity) {
        blockEntity.supplyingDirection = getSupplyingDirection(state);
        insertAndExtract(world, pos, state, blockEntity);
    }

    private static void insertAndExtract(@NotNull World world,
                                         @NotNull BlockPos pos,
                                         @NotNull BlockState state,
                                         @NotNull DirectedPipeBlockEntity<?> blockEntity) {
        if (world.isClient) {
            return;
        }
        boolean modified = false;
        if(!blockEntity.storage.isEmpty() && blockEntity.lastTicksToInsert > 0) {
            blockEntity.lastTicksToInsert--;
        }
        if (!blockEntity.storage.isEmpty() && blockEntity.lastTicksToInsert <= 0) {
            modified = PipeUtils.supply(world, pos, blockEntity, blockEntity.supplyingDirection);
            blockEntity.lastTicksToInsert = blockEntity.block.getTicksToInsert();
        }
        if (modified) {
            markDirty(world, pos, state);
        }
    }

    @Override
    public boolean canConsume(@NotNull ItemStack itemStack, @NotNull Direction direction) {
        if(world == null || direction.getOpposite() == supplyingDirection) {
            return false;
        }
        return storage.isEmpty() || storage.isItemEqual(itemStack) && storage.getCount() < storage.getMaxCount();
    }

    @Override
    public @NotNull ItemStack consume(@NotNull ItemStack itemStack, @NotNull Direction direction) {
        if(!canConsume(itemStack, direction)) {
            return itemStack;
        }
        return consume(itemStack);
    }

    private @NotNull ItemStack consume(@NotNull ItemStack itemStack) {
        markDirty();
        if(storage.isEmpty()) {
            storage = itemStack;
            return ItemStack.EMPTY;
        }
        int maxCount = storage.getMaxCount();
        int sumCount = storage.getCount() + itemStack.getCount();
        storage.setCount(Math.min(maxCount, sumCount));
        if(sumCount > maxCount) {
            return new ItemStack(itemStack.getItem(), sumCount - maxCount);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull List<ItemStack> canSupply(@NotNull Direction direction) {
        if(direction != supplyingDirection) {
            return Collections.emptyList();
        }
        return Collections.singletonList(storage.copy());
    }

    @Override
    public boolean supply(@NotNull ItemStack requested, @NotNull Direction direction) {
        if(direction != supplyingDirection || !storage.isItemEqual(requested)) {
            return false;
        }
        return supply(requested);
    }

    private static Direction getSupplyingDirection(@NotNull BlockState blockState) {
        return blockState.get(DirectedPipeBlock.FACING);
    }

    private boolean supply(@NotNull ItemStack requested) {
        int requestedCount = requested.getCount();
        int contains = storage.getCount();
        if (contains == 0 || contains < requestedCount) {
            return false;
        }
        storage.decrement(requestedCount);
        return true;
    }

    @Override
    public void returnStack(@NotNull ItemStack requested, @NotNull Direction direction) {
        consume(requested);
    }
}
