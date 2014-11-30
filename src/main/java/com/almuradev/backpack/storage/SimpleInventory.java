/**
 * This file is part of Backpack, All Rights Reserved.
 *
 * Copyright (c) 2014 AlmuraDev <http://github.com/AlmuraDev/>
 */
package com.almuradev.backpack.storage;

import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public class SimpleInventory extends InventoryBasic {

    private static final String TAG_COMPOUND = "ExtendedInventory";
    private static final String TAG_WORLDS = "Worlds";
    private static final String TAG_DIMENSIONS = "Dimensions";
    private static final String TAG_SLOTS = "Slots";
    private static final String TAG_WORLD = "World";
    private static final String TAG_DIMENSION = "Dimension";
    private static final String TAG_SLOT = "Slot";

    public SimpleInventory(String title, int slots) {
        super(title, true, slots);
    }

    public void fromNBT(String world, String dimension, NBTTagCompound compound) {
        if (compound == null) {
            return;
        }
        final NBTTagCompound inventoryCompound = compound.getCompoundTag(TAG_COMPOUND);
        final NBTTagList worldsList = inventoryCompound.getTagList(TAG_WORLDS, Constants.NBT.TAG_COMPOUND);
        NBTTagCompound worldCompound = null;

        //Find our world name in the list
        for (int i = 0; i < worldsList.tagCount(); i++) {
            final NBTTagCompound c = worldsList.getCompoundTagAt(i);
            if (c.getString(TAG_WORLD).equalsIgnoreCase(world)) {
                worldCompound = c;
                break;
            }
        }

        if (worldCompound == null) {
            return;
        }

        //Find our dimension in the world's compound
        final NBTTagList dimensionsList = worldCompound.getTagList(TAG_DIMENSIONS, Constants.NBT.TAG_COMPOUND);
        NBTTagCompound dimensionCompound = null;

        for (int i = 0; i < dimensionsList.tagCount(); i++) {
            final NBTTagCompound c = dimensionsList.getCompoundTagAt(i);
            if (c.getString(TAG_DIMENSION).equalsIgnoreCase(dimension)) {
                dimensionCompound = c;
                break;
            }
        }

        if (dimensionCompound == null) {
            return;
        }

        //Read in the slots
        final NBTTagList items = dimensionCompound.getTagList(TAG_SLOTS, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < items.tagCount(); i++) {
            final NBTTagCompound item = items.getCompoundTagAt(i);
            final int slotIndex = item.getInteger(TAG_SLOT);
            if (slotIndex >= 0 && slotIndex < getSizeInventory()) {
                setInventorySlotContents(slotIndex, ItemStack.loadItemStackFromNBT(item));
            }
        }
    }

    public NBTTagCompound toNBT(String world, String dimension, NBTTagCompound compound) {
        if (compound == null) {
            compound = new NBTTagCompound();
        }
        final NBTTagCompound inventoryCompound = compound.getCompoundTag(TAG_COMPOUND);
        final NBTTagList worldsList = inventoryCompound.getTagList(TAG_WORLDS, Constants.NBT.TAG_COMPOUND);
        NBTTagCompound worldCompound = null;

        //Find our world in the inventory's compound
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
        }

        //Find our provider in the world's compound
        final NBTTagList dimensionsList = worldCompound.getTagList(TAG_DIMENSIONS, Constants.NBT.TAG_COMPOUND);
        NBTTagCompound dimensionCompound = null;

        for (int i = 0; i < dimensionsList.tagCount(); i++) {
            final NBTTagCompound c = dimensionsList.getCompoundTagAt(i);
            if (c.getString(TAG_DIMENSION).equalsIgnoreCase(dimension)) {
                dimensionCompound = c;
                break;
            }
        }

        if (dimensionCompound == null) {
            dimensionCompound = new NBTTagCompound();
            dimensionCompound.setString(TAG_DIMENSION, dimension);
        }

        //Write our slots
        final NBTTagList slots = new NBTTagList();
        for (int i = 0; i < getSizeInventory(); i++) {
            final ItemStack stack = getStackInSlot(i);
            if (stack != null) {
                final NBTTagCompound item = new NBTTagCompound();
                item.setInteger(TAG_SLOT, i);
                stack.writeToNBT(item);
                slots.appendTag(item);
            }
        }
        //SLOTS -> DIMENSION
        dimensionCompound.setTag(TAG_SLOTS, slots);
        dimensionsList.appendTag(dimensionCompound);
        //DIMENSIONS -> WORLD
        worldCompound.setTag(TAG_DIMENSIONS, dimensionsList);
        worldsList.appendTag(worldCompound);
        //WORLDS -> INVENTORY
        inventoryCompound.setTag(TAG_WORLDS, worldsList);
        compound.setTag(TAG_COMPOUND, inventoryCompound);
        return compound;
    }
}
