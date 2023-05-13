package com.github.sib_energy_craft.pipes.filters.item_filter_extractor.block.entity;

import com.github.sib_energy_craft.pipes.api.ItemConsumer;
import com.github.sib_energy_craft.pipes.api.ItemSupplier;
import com.github.sib_energy_craft.pipes.filters.FilterUtils;
import com.github.sib_energy_craft.pipes.filters.ItemFilterMode;
import com.github.sib_energy_craft.pipes.filters.item_filter_extractor.block.ItemFilterExtractorBlock;
import com.github.sib_energy_craft.pipes.filters.item_filter_extractor.screen.ItemFilterExtractorScreenHandler;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

/**
 * @since 0.0.6
 * @author sibmaks
 */
public abstract class ItemFilterExtractorBlockEntity<T extends ItemFilterExtractorBlock>
        extends BlockEntity implements ExtendedScreenHandlerFactory, NamedScreenHandlerFactory, ItemSupplier {

    private final SimpleInventory filterInventory;
    private final SimpleInventory inventory;
    private final PropertyMap<ItemFilterExtractorBlockProperties> propertiesMap;
    private ItemFilterMode filterMode;

    private final T block;
    private int lastTicksToInsert;
    private int lastTicksToExtract;

    public ItemFilterExtractorBlockEntity(@NotNull BlockEntityType<? extends ItemFilterExtractorBlockEntity<T>> entityType,
                                          @NotNull T block,
                                          @NotNull BlockPos pos,
                                          @NotNull BlockState state) {
        super(entityType, pos, state);
        this.filterInventory = new SimpleInventory(25);
        this.filterInventory.addListener(it -> ItemFilterExtractorBlockEntity.this.markDirty());
        this.inventory = new SimpleInventory(25);
        this.inventory.addListener(it -> ItemFilterExtractorBlockEntity.this.markDirty());
        this.block = block;
        this.propertiesMap = new PropertyMap<>(ItemFilterExtractorBlockProperties.class);
        this.propertiesMap.add(ItemFilterExtractorBlockProperties.MODE, () -> filterMode.ordinal());
        this.filterMode = ItemFilterMode.WHITELIST;
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);
        var filterModeCode = nbt.getString("filterMode");
        this.filterMode = ItemFilterMode.valueOf(filterModeCode);
        var filterCompound = nbt.getCompound("filter");
        Inventories.readNbt(filterCompound, filterInventory.stacks);
        var inventoryCompound = nbt.getCompound("inventory");
        Inventories.readNbt(inventoryCompound, inventory.stacks);
    }

    @Override
    protected void writeNbt(@NotNull NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.putString("filterMode", filterMode.name());

        var filterCompound = new NbtCompound();
        Inventories.writeNbt(filterCompound, filterInventory.stacks);
        nbt.put("filter", filterCompound);

        var inventoryCompound = new NbtCompound();
        Inventories.writeNbt(inventoryCompound, inventory.stacks);
        nbt.put("inventory", inventoryCompound);
    }

    public static void serverTick(@NotNull World world,
                                  @NotNull BlockPos pos,
                                  @NotNull BlockState state,
                                  @NotNull ItemFilterExtractorBlockEntity<?> blockEntity) {
        insertAndExtract(world, pos, state, blockEntity, () -> extract(world, blockEntity, state));
    }

    private static void insertAndExtract(@NotNull World world,
                                         @NotNull BlockPos pos,
                                         @NotNull BlockState state,
                                         @NotNull ItemFilterExtractorBlockEntity<?> blockEntity,
                                         @NotNull BooleanSupplier booleanSupplier) {
        if (world.isClient) {
            return;
        }
        boolean modified = false;
        var inventory = blockEntity.inventory;
        if(!inventory.isEmpty() && blockEntity.lastTicksToInsert > 0) {
            blockEntity.lastTicksToInsert--;
        }
        if (!inventory.isEmpty() && blockEntity.lastTicksToInsert <= 0) {
            modified = insert(world, pos, state, inventory);
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
        return this.inventory.stacks.stream()
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
                                  @NotNull ItemFilterExtractorBlockEntity<?> extractor,
                                  @NotNull BlockState blockState) {
        var direction = blockState.get(ItemFilterExtractorBlock.FACING).getOpposite();
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

    private static boolean extract(@NotNull ItemFilterExtractorBlockEntity<?> extractor,
                                   @NotNull ItemSupplier itemSupplier,
                                   @NotNull ItemStack itemStack,
                                   @NotNull Direction side) {
        if(!FilterUtils.isValid(extractor.filterMode, extractor.filterInventory, itemStack)) {
            return false;
        }
        var consumingStack = new ItemStack(itemStack.getItem(), 1);
        if (!consumingStack.isEmpty() && itemSupplier.supply(consumingStack, side)) {
            var notTransferred = PipeUtils.transfer(extractor.inventory, consumingStack, side);
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
        var inputDirection = state.get(ItemFilterExtractorBlock.FACING).getOpposite();
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
                                                @NotNull ItemFilterExtractorBlockEntity<?> extractor,
                                                @NotNull Direction direction) {
        var pos = extractor.getPos();
        return PipeUtils.getItemSupplier(world, pos.offset(direction));
    }

    @Override
    public void writeScreenOpeningData(@NotNull ServerPlayerEntity player,
                                       @NotNull PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId,
                                    @NotNull PlayerInventory playerInventory,
                                    @NotNull PlayerEntity player) {
        return new ItemFilterExtractorScreenHandler(syncId, playerInventory, filterInventory, propertiesMap, world, pos);
    }

    @Override
    public @NotNull List<ItemStack> canSupply(@NotNull Direction direction) {
        if(world == null) {
            return Collections.emptyList();
        }
        var blockState = world.getBlockState(pos);
        var consumingDirection = blockState.get(ItemFilterExtractorBlock.FACING);
        if(direction == consumingDirection) {
            return Collections.emptyList();
        }
        return inventory.stacks.stream()
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
        var consumingDirection = blockState.get(ItemFilterExtractorBlock.FACING);
        if(direction == consumingDirection) {
            return false;
        }
        return PipeUtils.supply(this.inventory, requested);
    }

    @Override
    public void returnStack(@NotNull ItemStack requested, @NotNull Direction direction) {
        PipeUtils.consume(this.inventory, requested);
    }

    public void setMode(@NotNull ItemFilterMode nextMode) {
        this.filterMode = nextMode;
    }
}
