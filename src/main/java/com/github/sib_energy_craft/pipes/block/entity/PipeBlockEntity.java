package com.github.sib_energy_craft.pipes.block.entity;

import com.github.sib_energy_craft.IPipeBlock;
import com.github.sib_energy_craft.pipes.block.PipeBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import static com.github.sib_energy_craft.utils.PipeUtils.insert;

/**
 * @since 0.0.1
 * @author sibmaks
 */
public abstract class PipeBlockEntity<T extends PipeBlock> extends BlockEntity implements Inventory, IPipeBlock {
    private ItemStack storage;
    private Direction consumedDirection;

    private final T block;
    private int lastTicksToInsert;

    public PipeBlockEntity(@NotNull BlockEntityType<? extends PipeBlockEntity<T>> entityType,
                           @NotNull T block,
                           @NotNull BlockPos pos,
                           @NotNull BlockState state) {
        super(entityType, pos, state);
        this.block = block;
        this.storage = ItemStack.EMPTY;
        this.consumedDirection = Direction.UP;
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);
        storage = ItemStack.fromNbt(nbt.getCompound("storage"));
        consumedDirection = Direction.byName(nbt.getString("direction"));
    }

    @Override
    protected void writeNbt(@NotNull NbtCompound nbt) {
        super.writeNbt(nbt);

        var storageCompound = new NbtCompound();
        storage.writeNbt(storageCompound);
        nbt.put("storage", storageCompound);

        nbt.putString("direction", consumedDirection.getName());
    }

    @Override
    public int size() {
        return 1;
    }

    @NotNull
    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (slot != 0 || amount <= 0) {
            return ItemStack.EMPTY;
        }
        return storage.split(amount);
    }

    @Override
    public void setStack(@NotNull Direction direction,
                         @NotNull ItemStack stack) {
        this.consumedDirection = direction.getOpposite();
        this.storage = stack;
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
    }

    public static void serverTick(@NotNull World world,
                                  @NotNull BlockPos pos,
                                  @NotNull BlockState state,
                                  @NotNull PipeBlockEntity<?> blockEntity) {
        insertAndExtract(world, pos, state, blockEntity);
    }

    private static void insertAndExtract(@NotNull World world,
                                         @NotNull BlockPos pos,
                                         @NotNull BlockState state,
                                         @NotNull PipeBlockEntity<?> blockEntity) {
        if (world.isClient) {
            return;
        }
        boolean modified = false;
        if(!blockEntity.isEmpty() && blockEntity.lastTicksToInsert > 0) {
            blockEntity.lastTicksToInsert--;
        }
        if (!blockEntity.isEmpty() && blockEntity.lastTicksToInsert <= 0) {
            modified = insert(world, pos, blockEntity, blockEntity.consumedDirection);
            blockEntity.lastTicksToInsert = blockEntity.block.getTicksToInsert();
        }
        if (modified) {
            markDirty(world, pos, state);
        }
    }

    @Override
    public boolean isEmpty() {
        return storage.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        if (slot != 0) {
            return ItemStack.EMPTY;
        }
        return storage;
    }

    @NotNull
    @Override
    public ItemStack removeStack(int slot) {
        if (slot != 0) {
            return ItemStack.EMPTY;
        }
        var was = storage;
        this.storage = ItemStack.EMPTY;
        return was;
    }

    @Override
    public void setStack(int slot,
                         @NotNull ItemStack stack) {
        if (slot != 0) {
            return;
        }
        this.storage = stack;
    }

    @Override
    public boolean canPlayerUse(@NotNull PlayerEntity player) {
        return false;
    }

    @Override
    public void clear() {
        this.storage = ItemStack.EMPTY;
    }
}
