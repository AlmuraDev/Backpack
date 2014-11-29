/**
 * This file is part of Backpack, All Rights Reserved.
 *
 * Copyright (c) 2014 AlmuraDev <http://github.com/AlmuraDev/>
 */
package com.almuradev.backpack.client.network.play;

import com.almuradev.backpack.Backpack;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class C01BackpackOpenResponse implements IMessage, IMessageHandler<C01BackpackOpenResponse, IMessage> {

    @Override
    public void fromBytes(ByteBuf byteBuf) {
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
    }

    @Override
    public IMessage onMessage(C01BackpackOpenResponse message, MessageContext ctx) {
        if (ctx.side.isServer()) {
            final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            player.openGui(Backpack.INSTANCE, 0, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
        }
        return null;
    }
}
