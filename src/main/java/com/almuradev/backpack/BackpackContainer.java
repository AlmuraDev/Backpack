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
import net.minecraft.item.ItemStack;

public class BackpackContainer extends ContainerChest {

    public BackpackContainer(IInventory upperInventory, IInventory lowerInventory) {
        super(upperInventory, lowerInventory);
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
                        //TODO Switch to Backpack Item
                        if (stack != null && stack.getItem() == Items.bone) {
                            stack.setTagCompound(InventoryUtil.saveToNBT(getLowerChestInventory(), stack.getTagCompound()));
                        }
                        break;
                }
            }
        }
    }
}
