package com.github.sib_energy_craft.pipes.item_extractor.block.entity;

import com.github.sib_energy_craft.pipes.api.ItemConsumer;
import com.github.sib_energy_craft.pipes.api.ItemSupplier;
import com.github.sib_energy_craft.pipes.item_extractor.block.ItemExtractorBlock;
import com.github.sib_energy_craft.pipes.load.Screens;
import com.github.sib_energy_craft.pipes.utils.PipeUtils;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

/**
 * @since 0.0.1
 * @author sibmaks
 */
public abstract class ItemExtractorBlockEntity<T extends ItemExtractorBlock>
        extends LootableContainerBlockEntity implements ExtendedScreenHandlerFactory, ItemSupplier {
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(9, ItemStack.EMPTY);

    private final T block;
    private int lastTicksToInsert;
    private int lastTicksToExtract;

    public ItemExtractorBlockEntity(@NotNull BlockEntityType<? extends ItemExtractorBlockEntity<T>> entityType,
                                    @NotNull T block,
                                    @NotNull BlockPos pos,
                                    @NotNull BlockState state) {
        super(entityType, pos, state);
        this.block = block;
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!this.deserializeLootTable(nbt)) {
            Inventories.readNbt(nbt, this.inventory);
        }
    }

    @Override
    protected void writeNbt(@NotNull NbtCompound nbt) {
        super.writeNbt(nbt);
        if (!this.serializeLootTable(nbt)) {
            Inventories.writeNbt(nbt, this.inventory);
        }
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @NotNull
    @Override
    public ItemStack removeStack(int slot, int amount) {
        this.checkLootInteraction(null);
        return Inventories.splitStack(this.getInvStackList(), slot, amount);
    }

    @Override
    public void setStack(int slot, @NotNull ItemStack stack) {
        this.checkLootInteraction(null);
        this.getInvStackList().set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
    }

    public static void serverTick(@NotNull World world,
                                  @NotNull BlockPos pos,
                                  @NotNull BlockState state,
                                  @NotNull ItemExtractorBlockEntity<?> blockEntity) {
        insertAndExtract(world, pos, state, blockEntity, () -> extract(world, blockEntity, state));
    }

    private static void insertAndExtract(@NotNull World world,
                                         @NotNull BlockPos pos,
                                         @NotNull BlockState state,
                                         @NotNull ItemExtractorBlockEntity<?> blockEntity,
                                         @NotNull BooleanSupplier booleanSupplier) {
        if (world.isClient) {
            return;
        }
        boolean modified = false;
        if(!blockEntity.isEmpty() && blockEntity.lastTicksToInsert > 0) {
            blockEntity.lastTicksToInsert--;
        }
        if (!blockEntity.isEmpty() && blockEntity.lastTicksToInsert <= 0) {
            modified = insert(world, pos, state, blockEntity);
            blockEntity.lastTicksToInsert = blockEntity.block.getTicksToInsert();
        }
        if(blockEntity.hasSpace() && blockEntity.lastTicksToExtract > 0) {
            blockEntity.lastTicksToExtract--;
        }
        if (blockEntity.hasSpace() && blockEntity.lastTicksToExtract <= 0) {
            modified |= booleanSupplier.getAsBoolean();
            blockEntity.lastTicksToExtract = blockEntity.block.getTicksToExtract();
        }
        if (modified) {
            markDirty(world, pos, state);
        }
    }

    private boolean hasSpace() {
        return this.inventory.stream()
                .anyMatch(it -> it.isEmpty() || it.getCount() != it.getMaxCount());
    }

    private static boolean insert(@NotNull World world,
                                  @NotNull BlockPos pos,
                                  @NotNull BlockState state,
                                  @NotNull Inventory inventory) {
        var itemConsumers = getItemConsumers(world, pos, state);
        for (var entry : itemConsumers.entrySet()) {
            var direction = entry.getKey();
            var itemConsumer = entry.getValue();
            for (int i = 0; i < inventory.size(); ++i) {
                var inventoryStack = inventory.getStack(i);
                if (inventoryStack.isEmpty()) {
                    continue;
                }
                if(itemConsumer.canConsume(inventoryStack, direction)) {
                    var itemStack = inventoryStack.copy();
                    var notTransferred = itemConsumer.consume(inventory.removeStack(i, 1), direction);
                    if (notTransferred.isEmpty()) {
                        return true;
                    }
                    inventory.setStack(i, itemStack);
                }
            }
        }
        return false;
    }

    public static boolean extract(@NotNull World world,
                                  @NotNull ItemExtractorBlockEntity<?> extractor,
                                  @NotNull BlockState blockState) {
        var direction = blockState.get(ItemExtractorBlock.FACING).getOpposite();
        var itemSupplier = getItemSupplier(world, extractor, direction);
        if (itemSupplier == null) {
            return false;
        }
        var itemStacks = itemSupplier.canSupply(direction);
        if (itemStacks.isEmpty()) {
            return false;
        }
        return itemStacks.stream()
                .anyMatch(itemStack -> extract(extractor, itemSupplier, itemStack, direction));
    }

    private static boolean extract(@NotNull ItemExtractorBlockEntity<?> extractor,
                                   @NotNull ItemSupplier itemSupplier,
                                   @NotNull ItemStack itemStack,
                                   @NotNull Direction side) {
        var consumingStack = new ItemStack(itemStack.getItem(), 1);
        if (!consumingStack.isEmpty() && itemSupplier.supply(consumingStack, side)) {
            var notTransferred = PipeUtils.transfer(extractor, consumingStack, side);
            if (notTransferred.isEmpty()) {
                return true;
            }
            itemSupplier.returnStack(consumingStack, side);
        }
        return false;
    }

    @NotNull
    private static Map<Direction, ItemConsumer> getItemConsumers(@NotNull World world,
                                                                 @NotNull BlockPos pos,
                                                                 @NotNull BlockState state) {
        var inputDirection = state.get(ItemExtractorBlock.FACING).getOpposite();
        var inventories = new HashMap<Direction, ItemConsumer>();
        for (var outputDirection : Direction.values()) {
            if(outputDirection == inputDirection) {
                continue;
            }
            var itemConsumer = PipeUtils.getItemConsumer(world, pos.offset(outputDirection));
            if(itemConsumer != null) {
                inventories.put(outputDirection, itemConsumer);
            }
        }
        return inventories;
    }

    @Nullable
    private static ItemSupplier getItemSupplier(@NotNull World world,
                                                @NotNull ItemExtractorBlockEntity<?> extractor,
                                                @NotNull Direction direction) {
        var pos = extractor.getPos();
        return PipeUtils.getItemSupplier(world, pos.offset(direction));
    }

    @NotNull
    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        return this.inventory;
    }

    @Override
    protected void setInvStackList(@NotNull DefaultedList<ItemStack> list) {
        this.inventory = list;
    }

    @NotNull
    @Override
    protected ScreenHandler createScreenHandler(int syncId,
                                                @NotNull PlayerInventory playerInventory) {
        return new GenericContainerScreenHandler(Screens.GENERIC_9X1, syncId, playerInventory, this, 1);
    }

    @Override
    public void writeScreenOpeningData(@NotNull ServerPlayerEntity player,
                                       @NotNull PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    public @NotNull List<ItemStack> canSupply(@NotNull Direction direction) {
        if(world == null) {
            return Collections.emptyList();
        }
        var blockState = world.getBlockState(pos);
        var consumingDirection = blockState.get(ItemExtractorBlock.FACING);
        if(direction == consumingDirection) {
            return Collections.emptyList();
        }
        return inventory.stream()
                .filter(it -> !it.isEmpty())
                .map(ItemStack::copy)
                .collect(Collectors.toList());
    }

    @Override
    public boolean supply(@NotNull ItemStack requested, @NotNull Direction direction) {
        if(world == null) {
            return false;
        }
        var blockState = world.getBlockState(pos);
        var consumingDirection = blockState.get(ItemExtractorBlock.FACING);
        if(direction == consumingDirection) {
            return false;
        }
        return PipeUtils.supply(this, requested);
    }

    @Override
    public void returnStack(@NotNull ItemStack requested, @NotNull Direction direction) {
        PipeUtils.consume(this, requested);
    }
}
