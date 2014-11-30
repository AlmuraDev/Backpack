/**
 * This file is part of Backpack, All Rights Reserved.
 *
 * Copyright (c) 2014 AlmuraDev <http://github.com/AlmuraDev/>
 */
package com.almuradev.backpack;

import com.almuradev.backpack.server.BackpackDescriptor;
import com.almuradev.backpack.server.ServerProxy;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class BackpackContainer extends ContainerChest {
    private final IInventory upperInventory;

    public BackpackContainer(IInventory upperInventory, IInventory lowerInventory) {
        super(upperInventory, lowerInventory);
        this.upperInventory = upperInventory;
    }

    @Override
    public ItemStack slotClick(int index, int p_slotClick_2_, int p_slotClick_3_, EntityPlayer player) {
        final BackpackDescriptor descriptor = Backpack.PROXY.getDescriptor(player);

        if (descriptor != null && descriptor.type == 1) {
            if (index >= descriptor.size + upperInventory.getSizeInventory() - 13) {
                final ItemStack stack = upperInventory.getStackInSlot(index - (descriptor.size + upperInventory.getSizeInventory() - 13));
                if (stack != null && stack == player.getHeldItem()) {
                    return null;
                }
            }
        }
        return super.slotClick(index, p_slotClick_2_, p_slotClick_3_, player);
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);

        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            final BackpackDescriptor descriptor = ServerProxy.DESCRIPTOR_MAP.remove(player.getCommandSenderName());
            if (descriptor != null) {
                switch (descriptor.type) {
                    case 0:
                        InventoryUtil.saveToNBT(getLowerChestInventory(), player.worldObj.getWorldInfo().getWorldName(),
                                                player.worldObj.provider.dimensionId, player.getEntityData());
                        break;
                    case 1:
                        final ItemStack stack = player.getHeldItem();
                        if (stack != null && stack.getItem() == CommonProxy.ITEM_BACKPACK) {
                            stack.setTagCompound(InventoryUtil.saveToNBT(getLowerChestInventory(), stack.getTagCompound()));
                        }
                        break;
                }
            }
        }
    }
}
