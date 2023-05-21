package com.github.sib_energy_craft.pipes.trash_can.block.entity;

import com.github.sib_energy_craft.pipes.api.ItemConsumer;
import com.github.sib_energy_craft.pipes.load.ScreenHandlers;
import com.github.sib_energy_craft.pipes.trash_can.block.TrashCanBlock;
import com.github.sib_energy_craft.pipes.utils.PipeUtils;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.0.6
 * @author sibmaks
 */
public abstract class TrashCanBlockEntity<T extends TrashCanBlock>
        extends LootableContainerBlockEntity implements ExtendedScreenHandlerFactory, ItemConsumer {
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(9, ItemStack.EMPTY);
    private final ViewerCountManager stateManager = new ViewerCountManager(){

        @Override
        protected void onContainerOpen(@NotNull World world,
                                       @NotNull BlockPos pos,
                                       @NotNull BlockState state) {
            TrashCanBlockEntity.this.playSound(SoundEvents.BLOCK_BARREL_OPEN);
            TrashCanBlockEntity.this.setOpen(state, true);
        }

        @Override
        protected void onContainerClose(@NotNull World world,
                                        @NotNull BlockPos pos,
                                        @NotNull BlockState state) {
            TrashCanBlockEntity.this.playSound(SoundEvents.BLOCK_BARREL_CLOSE);
            TrashCanBlockEntity.this.setOpen(state, false);
        }

        @Override
        protected void onViewerCountUpdate(@NotNull World world,
                                           @NotNull BlockPos pos,
                                           @NotNull BlockState state,
                                           int oldViewerCount,
                                           int newViewerCount) {
        }

        @Override
        protected boolean isPlayerViewing(PlayerEntity player) {
            if (player.currentScreenHandler instanceof GenericContainerScreenHandler screenHandler) {
                var inventory = screenHandler.getInventory();
                return inventory == TrashCanBlockEntity.this;
            }
            return false;
        }
    };

    private final T block;
    private int lastTicksToRemove;

    public TrashCanBlockEntity(@NotNull BlockEntityType<? extends TrashCanBlockEntity<T>> entityType,
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
                                  @NotNull TrashCanBlockEntity<?> blockEntity) {
        insertAndExtract(world, pos, state, blockEntity);
    }

    private static void insertAndExtract(@NotNull World world,
                                         @NotNull BlockPos pos,
                                         @NotNull BlockState state,
                                         @NotNull TrashCanBlockEntity<?> blockEntity) {
        if (world.isClient) {
            return;
        }
        boolean modified = false;
        if(!blockEntity.isEmpty() && blockEntity.lastTicksToRemove > 0) {
            blockEntity.lastTicksToRemove--;
        }
        if (!blockEntity.isEmpty() && blockEntity.lastTicksToRemove <= 0) {
            modified = removeItem(blockEntity);
            blockEntity.lastTicksToRemove = blockEntity.block.getTicksRemove();
        }
        if (modified) {
            markDirty(world, pos, state);
        }
    }

    private static boolean removeItem(@NotNull Inventory inventory) {
        int size = inventory.size();
        for(int slot = 0; slot < size; slot++){
            var stack = inventory.getStack(slot);
            if(!stack.isEmpty()) {
                stack.decrement(1);
                if(stack.isEmpty()) {
                    inventory.setStack(slot, ItemStack.EMPTY);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void onOpen(@NotNull PlayerEntity player) {
        if (!this.removed && !player.isSpectator()) {
            this.stateManager.openContainer(player, world, pos, this.getCachedState());
        }
    }

    @Override
    public void onClose(@NotNull PlayerEntity player) {
        if (!this.removed && !player.isSpectator()) {
            this.stateManager.closeContainer(player, world, pos, this.getCachedState());
        }
    }

    public void tick() {
        if (!this.removed) {
            this.stateManager.updateViewerCount(world, pos, this.getCachedState());
        }
    }

    private void setOpen(@NotNull BlockState state, boolean opened) {
        if(world != null) {
            this.world.setBlockState(pos, state.with(TrashCanBlock.OPEN, opened), Block.NOTIFY_ALL);
        }
    }

    void playSound(@NotNull SoundEvent soundEvent) {
        var x = this.pos.getX();
        var y = this.pos.getY();
        var z = this.pos.getZ();
        if (world != null) {
            this.world.playSound(null, x, y, z, soundEvent, SoundCategory.BLOCKS, 0.5f, this.world.random.nextFloat() * 0.1f + 0.9f);
        }
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
        return new GenericContainerScreenHandler(ScreenHandlers.GENERIC_9X1, syncId, playerInventory, this, 1);
    }

    @Override
    public void writeScreenOpeningData(@NotNull ServerPlayerEntity player,
                                       @NotNull PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    public boolean canConsume(@NotNull ItemStack itemStack,
                              @NotNull Direction direction) {
        if(world == null) {
            return false;
        }
        return PipeUtils.hasSpaceFor(this, itemStack);
    }

    @Override
    public @NotNull ItemStack consume(@NotNull ItemStack itemStack,
                                      @NotNull Direction direction) {
        if(!canConsume(itemStack, direction)) {
            return itemStack;
        }
        return PipeUtils.consume(this, itemStack);
    }
}
