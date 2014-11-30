package com.almuradev.backpack;

import com.almuradev.backpack.client.network.play.C00BackpackOpenRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.List;

public class BackpackItem extends Item {

    public BackpackItem() {
        setUnlocalizedName(Backpack.MOD_ID + "_backpack");
        setTextureName(Backpack.MOD_ID + ":backpack.png");
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.tabTools);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote) {
            Backpack.NETWORK_FORGE.sendToServer(new C00BackpackOpenRequest(1));
        } else {
            final NBTTagCompound compound = stack.getTagCompound();
            if (compound == null) {
                stack.setTagCompound(InventoryUtil.initNBTFor("Backpack", 9, null));
            } else {
                final NBTTagCompound found = stack.getTagCompound().getCompoundTag(InventoryUtil.TAG_COMPOUND);
                if (found == null) {
                    stack.setTagCompound(InventoryUtil.initNBTFor("Backpack", 9, stack.getTagCompound()));
                }
            }
        }
        return stack;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        list.add("§6§oSize:§r " + InventoryUtil.getSizeFromNBT(stack.getTagCompound(), 9));
    }
}
