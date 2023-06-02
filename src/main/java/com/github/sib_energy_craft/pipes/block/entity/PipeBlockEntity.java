package com.github.sib_energy_craft.pipes.block.entity;

import com.github.sib_energy_craft.pipes.api.ItemConsumer;
import com.github.sib_energy_craft.pipes.api.ItemSupplier;
import com.github.sib_energy_craft.pipes.block.PipeBlock;
import com.github.sib_energy_craft.pipes.screen.OneSlotScreenHandler;
import com.github.sib_energy_craft.pipes.utils.PipeUtils;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * @since 0.0.1
 * @author sibmaks
 */
public abstract class PipeBlockEntity<T extends PipeBlock> extends BlockEntity
        implements ItemConsumer, ItemSupplier, ExtendedScreenHandlerFactory, Inventory {
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
        if(!blockEntity.storage.isEmpty() && blockEntity.lastTicksToInsert > 0) {
            blockEntity.lastTicksToInsert--;
        }
        if (!blockEntity.storage.isEmpty() && blockEntity.lastTicksToInsert <= 0) {
            modified = PipeUtils.supplyToAllExcept(world, pos, blockEntity, blockEntity.consumedDirection);
            blockEntity.lastTicksToInsert = blockEntity.block.getTicksToInsert();
        }
        if (modified) {
            markDirty(world, pos, state);
        }
    }

    @Override
    public boolean canConsume(@NotNull ItemStack itemStack, @NotNull Direction direction) {
        if(world == null) {
            return false;
        }
        return storage.isEmpty() || ItemStack.areItemsEqual(storage, itemStack) && storage.getCount() < storage.getMaxCount();
    }

    @Override
    public @NotNull ItemStack consume(@NotNull ItemStack itemStack, @NotNull Direction direction) {
        if(!canConsume(itemStack, direction)) {
            return itemStack;
        }
        this.consumedDirection = direction.getOpposite();
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
        if(direction == consumedDirection) {
            return Collections.emptyList();
        }
        return Collections.singletonList(storage.copy());
    }

    @Override
    public boolean supply(@NotNull ItemStack requested, @NotNull Direction direction) {
        if(direction == consumedDirection || !ItemStack.areItemsEqual(storage, requested)) {
            return false;
        }
        return supply(requested);
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

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId,
                                    @NotNull PlayerInventory playerInventory,
                                    @NotNull PlayerEntity player) {
        return new OneSlotScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {

    }

    @Override
    public int size() {
        return 1;
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

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if(slot != 0) {
            return ItemStack.EMPTY;
        }
        return storage.split(amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        if(slot != 0) {
            return ItemStack.EMPTY;
        }
        return storage.split(1);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if(slot != 0) {
            return;
        }
        this.storage = stack;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        this.storage = ItemStack.EMPTY;
    }
}
