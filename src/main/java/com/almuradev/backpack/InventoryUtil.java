/**
 * This file is part of Backpack, All Rights Reserved.
 *
 * Copyright (c) 2014 AlmuraDev <http://github.com/AlmuraDev/>
 */
package com.almuradev.backpack;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public class InventoryUtil {

    public static final String TAG_COMPOUND = "ExtendedInventory";
    public static final String TAG_WORLDS = "Worlds";
    public static final String TAG_DIMENSIONS = "Dimensions";
    public static final String TAG_SLOTS = "Slots";
    public static final String TAG_WORLD = "World";
    public static final String TAG_DIMENSION = "Dimension";
    public static final String TAG_SLOT = "Slot";
    public static final String TAG_TITLE = "Title";
    public static final String TAG_SIZE = "Size";

    /**
     * Loads an {@link IInventory} from a {@link NBTTagCompound} which is mapped to a World name and Dimension id.
     * @param inv The inventory to be populated
     * @param world The world name
     * @param dimension The dimension id
     * @param compound The compound to read from
     */
    public static IInventory loadFromNBT(IInventory inv, String world, int dimension, NBTTagCompound compound) {
        if (compound != null) {
            final NBTTagCompound inventoryCompound = compound.getCompoundTag(TAG_COMPOUND);
            final NBTTagList worldsList = inventoryCompound.getTagList(TAG_WORLDS, Constants.NBT.TAG_COMPOUND);
            NBTTagCompound worldCompound = null;

            for (int i = 0; i < worldsList.tagCount(); i++) {
                final NBTTagCompound c = worldsList.getCompoundTagAt(i);
                if (c.getString(TAG_WORLD).equalsIgnoreCase(world)) {
                    worldCompound = c;
                    break;
                }
            }

            if (worldCompound != null) {
                final NBTTagList dimensionsList = worldCompound.getTagList(TAG_DIMENSIONS, Constants.NBT.TAG_COMPOUND);
                NBTTagCompound dimensionCompound = null;

                for (int i = 0; i < dimensionsList.tagCount(); i++) {
                    final NBTTagCompound c = dimensionsList.getCompoundTagAt(i);
                    if (c.getInteger(TAG_DIMENSION) == dimension) {
                        dimensionCompound = c;
                        break;
                    }
                }

                if (dimensionCompound != null) {
                    final NBTTagList items = dimensionCompound.getTagList(TAG_SLOTS, Constants.NBT.TAG_COMPOUND);
                    for (int i = 0; i < items.tagCount(); i++) {
                        final NBTTagCompound item = items.getCompoundTagAt(i);
                        final int slotIndex = item.getInteger(TAG_SLOT);
                        if (slotIndex >= 0 && slotIndex < inv.getSizeInventory()) {
                            inv.setInventorySlotContents(slotIndex, ItemStack.loadItemStackFromNBT(item));
                        }
                    }
                }
            }
        }
        return inv;
    }

    /**
     * Loads an {@link IInventory} from a {@link NBTTagCompound}.
     * @param compound The compound to read from
     */
    public static IInventory loadFromNBT(NBTTagCompound compound) {
        if (compound != null) {
            final NBTTagCompound inventoryCompound = compound.getCompoundTag(TAG_COMPOUND);
            final String title = inventoryCompound.getString(TAG_TITLE);
            final int size = inventoryCompound.getInteger(TAG_SIZE);
            final InventoryBasic inv = new InventoryBasic(title, true, size);
            final NBTTagList items = inventoryCompound.getTagList(TAG_SLOTS, Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < items.tagCount(); i++) {
                final NBTTagCompound item = items.getCompoundTagAt(i);
                final int slotIndex = item.getInteger(TAG_SLOT);
                if (slotIndex >= 0 && slotIndex < inv.getSizeInventory()) {
                    inv.setInventorySlotContents(slotIndex, ItemStack.loadItemStackFromNBT(item));
                }
            }
            return inv;
        }
        return null;
    }

    /**
     * Saves an {@link IInventory} to a {@link NBTTagCompound} using a World's name and dimension id as keys.
     * @param inv The inventory to save
     * @param world The world name
     * @param dimension The dimension id
     * @param compound The compound to save into
     * @return The compound with the data saved
     */
    public static NBTTagCompound saveToNBT(IInventory inv, String world, int dimension, NBTTagCompound compound) {
        if (compound == null) {
            compound = new NBTTagCompound();
        }
        final NBTTagCompound inventoryCompound = compound.getCompoundTag(TAG_COMPOUND);
        final NBTTagList worldsList = inventoryCompound.getTagList(TAG_WORLDS, Constants.NBT.TAG_COMPOUND);
        NBTTagCompound worldCompound = null;

        for (int i = 0; i < worldsList.tagCount(); i++) {
            final NBTTagCompound c = worldsList.getCompoundTagAt(i);
            if (c.getString(TAG_WORLD).equalsIgnoreCase(world)) {
                worldCompound = c;
                break;
            }
        }

        if (worldCompound == null) {
            worldCompound = new NBTTagCompound();
            worldCompound.setString(TAG_WORLD, world);
            worldsList.appendTag(worldCompound);
        }

        final NBTTagList dimensionsList = worldCompound.getTagList(TAG_DIMENSIONS, Constants.NBT.TAG_COMPOUND);
        NBTTagCompound dimensionCompound = null;

        for (int i = 0; i < dimensionsList.tagCount(); i++) {
            final NBTTagCompound c = dimensionsList.getCompoundTagAt(i);
            if (c.getInteger(TAG_DIMENSION) == dimension) {
                dimensionCompound = c;
                break;
            }
        }

        if (dimensionCompound == null) {
            dimensionCompound = new NBTTagCompound();
            dimensionCompound.setInteger(TAG_DIMENSION, dimension);
            dimensionsList.appendTag(dimensionCompound);
        }

        final NBTTagList slots = new NBTTagList();
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            final ItemStack stack = inv.getStackInSlot(i);
            if (stack != null) {
                final NBTTagCompound item = new NBTTagCompound();
                item.setInteger(TAG_SLOT, i);
                stack.writeToNBT(item);
                slots.appendTag(item);
            }
        }
        dimensionCompound.setTag(TAG_SLOTS, slots);
        worldCompound.setTag(TAG_DIMENSIONS, dimensionsList);
        inventoryCompound.setTag(TAG_WORLDS, worldsList);
        compound.setTag(TAG_COMPOUND, inventoryCompound);
        return compound;
    }

    /**
     * Saves an {@link IInventory} to a {@link NBTTagCompound}.
     * @param inv The inventory to save
     * @param compound The compound to save into
     * @return The compound with the data saved
     */
    public static NBTTagCompound saveToNBT(IInventory inv, NBTTagCompound compound) {
        if (compound == null) {
            compound = new NBTTagCompound();
        }

        final NBTTagCompound inventoryCompound = compound.getCompoundTag(TAG_COMPOUND);
        inventoryCompound.setString(TAG_TITLE, inv.getInventoryName());
        inventoryCompound.setInteger(TAG_SIZE, inv.getSizeInventory());

        final NBTTagList slots = new NBTTagList();
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            final ItemStack stack = inv.getStackInSlot(i);
            if (stack != null) {
                final NBTTagCompound item = new NBTTagCompound();
                item.setInteger(TAG_SLOT, i);
                stack.writeToNBT(item);
                slots.appendTag(item);
            }
        }
        inventoryCompound.setTag(TAG_SLOTS, slots);
        compound.setTag(TAG_COMPOUND, inventoryCompound);
        return compound;
    }

    public static NBTTagCompound initNBTFor(String title, int size, NBTTagCompound compound) {
        if (compound == null) {
            compound = new NBTTagCompound();
        }

        final NBTTagCompound inventoryCompound = compound.getCompoundTag(TAG_COMPOUND);
        inventoryCompound.setString(TAG_TITLE, title);
        inventoryCompound.setInteger(TAG_SIZE, size);
        compound.setTag(TAG_COMPOUND, inventoryCompound);
        return compound;
    }

    public static int getSizeFromNBT(NBTTagCompound compound, int fallback) {
        if (compound != null) {
            int size = compound.getCompoundTag(TAG_COMPOUND).getInteger(TAG_SIZE);
            if (size >= 9 && size <= 54 && size % 9 == 0) {
                return size;
            }
        }
        return fallback;
    }

    public static NBTTagCompound setSizeFor(int size, NBTTagCompound compound) {
        if (size >= 9 && size <= 54 && size % 9 == 0) {
            compound.getCompoundTag(TAG_COMPOUND).setInteger(TAG_SIZE, size);
        }
        return compound;
    }

    public static String getTitleFromNBT(NBTTagCompound compound, String fallback) {
        if (compound != null) {
            final String title = compound.getCompoundTag(TAG_COMPOUND).getString(TAG_TITLE);
            if (!title.isEmpty()) {
                return title;
            }
        }
        return fallback;
    }

    public static NBTTagCompound setTitleFor(String title, NBTTagCompound compound) {
        if (!title.isEmpty()) {
            compound.getCompoundTag(TAG_COMPOUND).setString(TAG_TITLE, title);
        }
        return compound;
    }
}
