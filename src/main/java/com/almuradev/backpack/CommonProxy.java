/**
 * This file is part of Backpack, All Rights Reserved.
 *
 * Copyright (c) 2014 AlmuraDev <http://github.com/AlmuraDev/>
 */
package com.almuradev.backpack;

import com.almuradev.backpack.client.BackpackGui;
import com.almuradev.backpack.client.ClientProxy;
import com.almuradev.backpack.client.network.play.C00BackpackOpenRequest;
import com.almuradev.backpack.client.network.play.C01BackpackOpenResponse;
import com.almuradev.backpack.server.BackpackDescriptor;
import com.almuradev.backpack.server.ServerProxy;
import com.almuradev.backpack.server.network.play.S00BackpackOpenRequest;
import com.almuradev.backpack.storage.BackpackContainer;
import com.almuradev.backpack.storage.SimpleInventory;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class CommonProxy implements IGuiHandler {

    public void onPreInitialization(FMLPreInitializationEvent event) {
        Backpack.NETWORK_FORGE.registerMessage(C00BackpackOpenRequest.class, C00BackpackOpenRequest.class, 0, Side.SERVER);
        Backpack.NETWORK_FORGE.registerMessage(C01BackpackOpenResponse.class, C01BackpackOpenResponse.class, 1, Side.SERVER);
        Backpack.NETWORK_FORGE.registerMessage(S00BackpackOpenRequest.class, S00BackpackOpenRequest.class, 2, Side.CLIENT);
        NetworkRegistry.INSTANCE.registerGuiHandler(Backpack.INSTANCE, new CommonProxy());
    }

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        final BackpackDescriptor descriptor = ServerProxy.DESCRIPTOR_MAP.get(player.getCommandSenderName());
        if (descriptor != null) {
            final SimpleInventory inventory = new SimpleInventory(descriptor.title, descriptor.size);
            switch (id) {
                case 0:
                    inventory.fromNBT(player.worldObj.getWorldInfo().getWorldName(), player.worldObj.provider.getDimensionName(),
                                      player.getEntityData());
                    break;
                case 1:
                    final ItemStack stack = player.getHeldItem();
                    //TODO Switch this to Backpack item
                    if (stack != null && stack.getItem() == Items.bone) {
                        inventory.fromNBT(player.worldObj.getWorldInfo().getWorldName(), player.worldObj.provider.getDimensionName(),
                                          stack.getTagCompound());
                    }
                    break;
            }
            return new BackpackContainer(player.inventory, inventory);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        return new BackpackGui(player.inventory, new SimpleInventory(ClientProxy.title, ClientProxy.size));
    }
}
