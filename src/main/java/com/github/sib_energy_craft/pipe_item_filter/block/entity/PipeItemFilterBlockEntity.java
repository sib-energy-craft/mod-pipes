package com.github.sib_energy_craft.pipe_item_filter.block.entity;

import com.github.sib_energy_craft.IPipeBlock;
import com.github.sib_energy_craft.item_filter.item.ItemFilterItem;
import com.github.sib_energy_craft.pipe_item_filter.block.PipeItemFilterBlock;
import com.github.sib_energy_craft.pipe_item_filter.screen.PipeItemFilterItemScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
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

import static com.github.sib_energy_craft.utils.PipeUtils.insert;

/**
 * @since 0.0.1
 * @author sibmaks
 */
public abstract class PipeItemFilterBlockEntity<T extends PipeItemFilterBlock> extends BlockEntity
        implements Inventory, IPipeBlock, NamedScreenHandlerFactory, ExtendedScreenHandlerFactory {

    private SimpleInventory filterInventory;
    private ItemStack storage;
    private Direction consumedDirection;

    private final T block;
    private int lastTicksToInsert;

    public PipeItemFilterBlockEntity(@NotNull BlockEntityType<? extends PipeItemFilterBlockEntity<T>> entityType,
                                     @NotNull T block,
                                     @NotNull BlockPos pos,
                                     @NotNull BlockState state) {
        super(entityType, pos, state);
        this.block = block;
        this.storage = ItemStack.EMPTY;
        this.consumedDirection = Direction.UP;
        this.filterInventory = new SimpleInventory(ItemStack.EMPTY);
        this.filterInventory.addListener(it -> markDirty());
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);
        var filterStack = ItemStack.fromNbt(nbt.getCompound("filter"));
        filterInventory.setStack(0, filterStack);
        storage = ItemStack.fromNbt(nbt.getCompound("storage"));
        consumedDirection = Direction.byName(nbt.getString("direction"));
    }

    @Override
    protected void writeNbt(@NotNull NbtCompound nbt) {
        super.writeNbt(nbt);

        var filterCompound = new NbtCompound();
        var filterStack = filterInventory.getStack(0);
        filterStack.writeNbt(filterCompound);
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
        if (filterInventory.isEmpty()) {
            return true;
        }
        var filterStack = filterInventory.getStack(0);
        if (filterStack.getItem() instanceof ItemFilterItem filterItem) {
            var mode = filterItem.getMode(filterStack);
            switch (mode) {
                case OFF -> {
                    return true;
                }
                case WHITELIST -> {
                    var stackItem = stack.getItem();
                    var inventory = filterItem.getInventory(filterStack);
                    return inventory.stream().anyMatch(stackItem::equals);
                }
                case BLACKLIST -> {
                    var stackItem = stack.getItem();
                    var inventory = filterItem.getInventory(filterStack);
                    return inventory.stream().noneMatch(stackItem::equals);
                }
            }
        }
        return true;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId,
                                    @NotNull PlayerInventory playerInventory,
                                    @NotNull PlayerEntity player) {
        return new PipeItemFilterItemScreenHandler(syncId, playerInventory, filterInventory);
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
}
