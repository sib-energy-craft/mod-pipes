package com.github.sib_energy_craft.pipes.filters.item_filter.block.entity;

import com.github.sib_energy_craft.pipes.filters.FilterUtils;
import com.github.sib_energy_craft.pipes.filters.ItemFilterMode;
import com.github.sib_energy_craft.pipes.filters.item_filter.block.PipeItemFilterBlock;
import com.github.sib_energy_craft.pipes.filters.item_filter.screen.PipeItemFilterScreenHandler;
import com.github.sib_energy_craft.pipes.api.ItemConsumer;
import com.github.sib_energy_craft.pipes.api.ItemSupplier;
import com.github.sib_energy_craft.pipes.utils.PipeUtils;
import com.github.sib_energy_craft.sec_utils.screen.PropertyMap;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
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
public abstract class PipeItemFilterBlockEntity<T extends PipeItemFilterBlock> extends BlockEntity
        implements Inventory, ItemConsumer, ItemSupplier, NamedScreenHandlerFactory, ExtendedScreenHandlerFactory {

    private final SimpleInventory filterInventory;
    private ItemStack storage;
    private Direction consumedDirection;
    private ItemFilterMode filterMode;
    private final PropertyMap<PipeItemFilterBlockProperties> propertiesMap;

    private final T block;
    private int lastTicksToInsert;

    public PipeItemFilterBlockEntity(@NotNull BlockEntityType<? extends PipeItemFilterBlockEntity<T>> entityType,
                                     @NotNull T block,
                                     @NotNull BlockPos pos,
                                     @NotNull BlockState state) {
        super(entityType, pos, state);
        this.block = block;
        this.filterMode = ItemFilterMode.WHITELIST;
        this.storage = ItemStack.EMPTY;
        this.consumedDirection = Direction.UP;
        this.filterInventory = new SimpleInventory(25);
        this.filterInventory.addListener(it -> PipeItemFilterBlockEntity.this.markDirty());
        this.propertiesMap = new PropertyMap<>(PipeItemFilterBlockProperties.class);
        this.propertiesMap.add(PipeItemFilterBlockProperties.MODE, () -> filterMode.ordinal());
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);
        var filterModeCode = nbt.getString("filterMode");
        this.filterMode = ItemFilterMode.valueOf(filterModeCode);
        var filterCompound = nbt.getCompound("filter");
        Inventories.readNbt(filterCompound, filterInventory.stacks);
        storage = ItemStack.fromNbt(nbt.getCompound("storage"));
        consumedDirection = Direction.byName(nbt.getString("direction"));
    }

    @Override
    protected void writeNbt(@NotNull NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.putString("filterMode", filterMode.name());

        var filterCompound = new NbtCompound();
        Inventories.writeNbt(filterCompound, filterInventory.stacks);
        nbt.put("filter", filterCompound);

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

    public static void serverTick(@NotNull World world,
                                  @NotNull BlockPos pos,
                                  @NotNull BlockState state,
                                  @NotNull PipeItemFilterBlockEntity<?> blockEntity) {
        insertAndExtract(world, pos, state, blockEntity);
    }

    private static void insertAndExtract(@NotNull World world,
                                         @NotNull BlockPos pos,
                                         @NotNull BlockState state,
                                         @NotNull PipeItemFilterBlockEntity<?> blockEntity) {
        if (world.isClient) {
            return;
        }
        boolean modified = false;
        if(!blockEntity.isEmpty() && blockEntity.lastTicksToInsert > 0) {
            blockEntity.lastTicksToInsert--;
        }
        if (!blockEntity.isEmpty() && blockEntity.lastTicksToInsert <= 0) {
            modified = PipeUtils.insert(world, pos, blockEntity, blockEntity.consumedDirection);
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
        if(slot != 0) {
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
    public void clear() {
        this.storage = ItemStack.EMPTY;
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return FilterUtils.isValid(filterMode, filterInventory, stack);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId,
                                    @NotNull PlayerInventory playerInventory,
                                    @NotNull PlayerEntity player) {
        return new PipeItemFilterScreenHandler(syncId, playerInventory, filterInventory, propertiesMap, world, pos);
    }

    @Override
    public boolean canPlayerUse(@NotNull PlayerEntity player) {
        return true;
    }

    @Override
    public void writeScreenOpeningData(@NotNull ServerPlayerEntity player,
                                       @NotNull PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    public void setMode(@NotNull ItemFilterMode mode) {
        this.filterMode = mode;
    }

    @Override
    public boolean canConsume(@NotNull ItemStack itemStack, @NotNull Direction direction) {
        if(world == null) {
            return false;
        }
        if(!FilterUtils.isValid(filterMode, filterInventory, itemStack)) {
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
}
