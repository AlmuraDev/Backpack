/**
 * This file is part of Backpack, All Rights Reserved.
 *
 * Copyright (c) 2014 AlmuraDev <http://github.com/AlmuraDev/>
 */
package com.almuradev.backpack.client.network.play;

import com.almuradev.backpack.CommonProxy;
import com.almuradev.backpack.InventoryUtil;
import com.almuradev.backpack.server.BackpackDescriptor;
import com.almuradev.backpack.server.ServerProxy;
import com.almuradev.backpack.server.network.play.S00BackpackOpenRequest;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class C00BackpackOpenRequest implements IMessage, IMessageHandler<C00BackpackOpenRequest, S00BackpackOpenRequest> {

    public int type;

    public C00BackpackOpenRequest() {
    }

    public C00BackpackOpenRequest(int type) {
        this.type = type;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        type = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(type);
    }

    @Override
    public S00BackpackOpenRequest onMessage(C00BackpackOpenRequest message, MessageContext ctx) {
        if (ctx.side.isServer()) {
            final BackpackDescriptor descriptor = buildFromType(message, ctx);
            if (descriptor == null) {
                return null;
            }
            ServerProxy.DESCRIPTOR_MAP.put(ctx.getServerHandler().playerEntity.getCommandSenderName(), descriptor);
            return new S00BackpackOpenRequest(descriptor.type, descriptor.title, descriptor.size);
        }
        return null;
    }

    private BackpackDescriptor buildFromType(C00BackpackOpenRequest message, MessageContext ctx) {
        switch (message.type) {
            // Keyboard
            case 0:
                return new BackpackDescriptor(message.type, "\u00A79Backpack", 54);
            // Item in hand
            case 1:
                final EntityPlayer player = ctx.getServerHandler().playerEntity;
                final ItemStack stack = player.getHeldItem();
                if (stack != null && stack.getItem() == CommonProxy.ITEM_BACKPACK) {
                    return new BackpackDescriptor(message.type, InventoryUtil.getTitleFromNBT(stack.getTagCompound(), "Backpack"),
                                                  InventoryUtil.getSizeFromNBT(stack.getTagCompound(), 9));
                }
        }
        return null;
    }
}
