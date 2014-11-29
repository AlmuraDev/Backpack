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
    private static final String TAG_LIST_NAME = "ExtendedInventory";
    private static final String TAG_SLOT_NAME = "Slot";
    public SimpleInventory(String title, int slots) {
        super(title, true, slots);
    }

    public void fromNBT(NBTTagCompound compound) {
        final NBTTagList items = compound.getTagList(TAG_LIST_NAME, Constants.NBT.TAG_COMPOUND);
        if (items == null) {
            return;
        }

        for (int i = 0; i < items.tagCount(); i++) {
            final NBTTagCompound item = items.getCompoundTagAt(i);
            final int slotIndex = item.getInteger(TAG_SLOT_NAME);
            if (slotIndex >= 0 && slotIndex < getSizeInventory()) {
                setInventorySlotContents(slotIndex, ItemStack.loadItemStackFromNBT(item));
            }
        }
    }

    public void toNBT(NBTTagCompound compound) {
        final NBTTagList items = new NBTTagList();

        for (int i = 0; i < getSizeInventory(); i++) {
            final ItemStack stack = getStackInSlot(i);
            if (stack != null) {
                final NBTTagCompound item = new NBTTagCompound();
                item.setInteger(TAG_SLOT_NAME, i);
                stack.writeToNBT(item);
                items.appendTag(item);
            }
        }

        compound.setTag(TAG_LIST_NAME, items);
    }
}
