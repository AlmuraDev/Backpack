/**
 * This file is part of Backpack, All Rights Reserved.
 *
 * Copyright (c) 2014 AlmuraDev <http://github.com/AlmuraDev/>
 */
package com.almuradev.backpack.storage;

import com.almuradev.backpack.Backpack;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;

public class BackpackContainer extends ContainerChest {
    public BackpackContainer(IInventory upperInventory, IInventory lowerInventory) {
        super(upperInventory, lowerInventory);
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);

        Backpack.LOGGER.info("Container closed on Side [" + FMLCommonHandler.instance().getEffectiveSide() + "]");

        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            ((SimpleInventory) getLowerChestInventory()).toNBT(player.getEntityData());
        }
    }
}
