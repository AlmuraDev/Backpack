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
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class CommonProxy implements IGuiHandler {

    public static final BackpackItem ITEM_BACKPACK = new BackpackItem();
    public static IRecipe RECIPE_CREATE_BACKPACK;
    public static IRecipe RECIPE_UPGRADE_BACKPACK;

    public void onPreInitialization(FMLPreInitializationEvent event) {
        Backpack.NETWORK_FORGE.registerMessage(C00BackpackOpenRequest.class, C00BackpackOpenRequest.class, 0, Side.SERVER);
        Backpack.NETWORK_FORGE.registerMessage(C01BackpackOpenResponse.class, C01BackpackOpenResponse.class, 1, Side.SERVER);
        Backpack.NETWORK_FORGE.registerMessage(S00BackpackOpenRequest.class, S00BackpackOpenRequest.class, 2, Side.CLIENT);
        NetworkRegistry.INSTANCE.registerGuiHandler(Backpack.INSTANCE, new CommonProxy());
        GameRegistry.registerItem(ITEM_BACKPACK, ITEM_BACKPACK.getUnlocalizedName());

        RECIPE_CREATE_BACKPACK = GameRegistry.addShapedRecipe(new ItemStack(ITEM_BACKPACK),
                                                              "LDL",
                                                              "LEL",
                                                              "LSL",
                                                              'L', Items.leather,
                                                              'D', Items.diamond,
                                                              'E', Blocks.ender_chest,
                                                              'S', Items.string);
        RECIPE_UPGRADE_BACKPACK = GameRegistry.addShapedRecipe(new ItemStack(ITEM_BACKPACK),
                                                               "DDD",
                                                               " B ",
                                                               "EEE",
                                                               'D', Items.diamond,
                                                               'B', ITEM_BACKPACK,
                                                               'E', Items.emerald);
        FMLCommonHandler.instance().bus().register(this);
    }

    public BackpackDescriptor getDescriptor(EntityPlayer player) {
        return null;
    }

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        final BackpackDescriptor descriptor = ServerProxy.DESCRIPTOR_MAP.get(player.getCommandSenderName());
        if (descriptor != null) {
            IInventory inventory = null;
            switch (id) {
                case 0:
                    inventory = InventoryUtil.loadFromNBT(new InventoryBasic(descriptor.title, true, descriptor.size),
                                                          player.worldObj.getWorldInfo().getWorldName(), player.worldObj.provider.dimensionId,
                                                          player.getEntityData());
                    break;
                case 1:
                    final ItemStack stack = player.getHeldItem();
                    if (stack != null && stack.getItem() == CommonProxy.ITEM_BACKPACK) {
                        inventory = InventoryUtil.loadFromNBT(stack.getTagCompound());
                    }
                    break;
                default:
                    return null;
            }
            return new BackpackContainer(player.inventory, inventory);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        return new BackpackGui(player.inventory, new InventoryBasic(ClientProxy.descriptor.title, true, ClientProxy.descriptor.size));
    }

    @SubscribeEvent
    public void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (!event.player.worldObj.isRemote) {
            if (RECIPE_CREATE_BACKPACK.matches((InventoryCrafting) event.craftMatrix, event.player.worldObj)) {
                event.crafting.setTagCompound(InventoryUtil.initNBTFor("Backpack", 9, event.crafting.getTagCompound()));
            } else if (RECIPE_UPGRADE_BACKPACK.matches((InventoryCrafting) event.craftMatrix, event.player.worldObj)) {
                final ItemStack previousBackpack = event.craftMatrix.getStackInSlot(4);
                final int size = InventoryUtil.getSizeFromNBT(previousBackpack.getTagCompound(), 9);

                event.crafting.setTagCompound((NBTTagCompound) previousBackpack.getTagCompound().copy());
                if (size < 54) {
                    InventoryUtil.setSizeFor(size + 9, event.crafting.getTagCompound());
                }
            }
        }
    }
}
