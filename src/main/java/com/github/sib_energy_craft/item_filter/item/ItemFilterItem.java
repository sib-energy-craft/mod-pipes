package com.github.sib_energy_craft.item_filter.item;

import com.github.sib_energy_craft.item_filter.ItemFilterMode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.0.1
 * @author sibmaks
 */
public class ItemFilterItem extends Item {
    public static final int SIZE = 25;
    public static final String MODE = "Mode";
    public static final String INVENTORY = "Inventory";

    public static final ItemFilterMode[] MODES = ItemFilterMode.values();

    public ItemFilterItem(@NotNull Settings settings) {
        super(settings);
    }

    @NotNull
    @Override
    public TypedActionResult<ItemStack> use(@NotNull World world,
                                            @NotNull PlayerEntity player,
                                            @NotNull Hand hand) {
        var itemStack = player.getStackInHand(hand);
        player.openHandledScreen(new ItemFilterItemScreenHandlerFactory(world, itemStack));
        player.incrementStat(Stats.USED.getOrCreateStat(this));
        return TypedActionResult.success(itemStack, world.isClient());
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        createDefaultNbt(stack);
    }

    private static NbtCompound createDefaultNbt(ItemStack stack) {
        var nbt = stack.getOrCreateNbt();
        nbt.putString(MODE, ItemFilterMode.WHITELIST.name());

        var inventory = DefaultedList.ofSize(SIZE, Items.AIR);
        updateInventory(nbt, inventory);
        return nbt;
    }

    private static void updateInventory(@NotNull NbtCompound nbt,
                                        @NotNull DefaultedList<Item> inventory) {
        var nbtList = new NbtList();
        for (var item : inventory) {
            var nbtCompound = new NbtCompound();
            var identifier = Registries.ITEM.getId(item);
            nbtCompound.putString("id", identifier.toString());
            nbtList.add(nbtCompound);
        }
        nbt.put(INVENTORY, nbtList);
    }

    @NotNull
    public ItemFilterMode getMode(@NotNull ItemStack itemStack) {
        var nbt = itemStack.getNbt();
        if(nbt == null) {
            nbt = createDefaultNbt(itemStack);
        }
        var mode = nbt.getString(MODE);
        return ItemFilterMode.valueOf(mode);
    }

    public void nextMode(@NotNull ItemStack itemStack) {
        var nbt = itemStack.getNbt();
        if(nbt == null) {
            nbt = createDefaultNbt(itemStack);
        }
        var modeCode = nbt.getString(MODE);
        var mode = ItemFilterMode.valueOf(modeCode);
        var nextMode = MODES[(mode.ordinal() + 1) % MODES.length];
        nbt.putString(MODE, nextMode.name());
    }

    @NotNull
    public DefaultedList<Item> getInventory(@NotNull ItemStack itemStack) {
        var nbt = itemStack.getNbt();
        if(nbt == null) {
            return DefaultedList.ofSize(SIZE, Items.AIR);
        }
        var items = DefaultedList.ofSize(SIZE, Items.AIR);
        var nbtElements = nbt.getList(INVENTORY, NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < nbtElements.size(); i++) {
            var nbtCompound = (NbtCompound) nbtElements.get(i);
            var itemId = nbtCompound.getString("id");
            var item = Registries.ITEM.get(new Identifier(itemId));
            items.set(i, item);
        }
        return items;
    }

    public void setItem(@NotNull ItemStack itemStack,
                        int index,
                        @NotNull Item item) {
        var inventory = getInventory(itemStack);
        inventory.set(index, item);
        var nbt = itemStack.getNbt();
        if(nbt == null) {
            nbt = createDefaultNbt(itemStack);
        }
        updateInventory(nbt, inventory);
    }
}
