/**
 * This file is part of Backpack, All Rights Reserved.
 *
 * Copyright (c) 2014 AlmuraDev <http://github.com/AlmuraDev/>
 */
package com.almuradev.backpack.client.network.play;

import com.almuradev.backpack.server.network.play.S00BackpackOpenRequest;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class C00BackpackOpenRequest implements IMessage, IMessageHandler<C00BackpackOpenRequest, S00BackpackOpenRequest> {

    @Override
    public void fromBytes(ByteBuf byteBuf) {
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
    }

    @Override
    public S00BackpackOpenRequest onMessage(C00BackpackOpenRequest message, MessageContext ctx) {
        if (ctx.side.isServer()) {
            return new S00BackpackOpenRequest("Backpack", 9);
        }
        return null;
    }
}
