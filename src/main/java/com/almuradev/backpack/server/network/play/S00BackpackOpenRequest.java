/**
 * This file is part of Backpack, All Rights Reserved.
 *
 * Copyright (c) 2014 AlmuraDev <http://github.com/AlmuraDev/>
 */
package com.almuradev.backpack.server.network.play;

import com.almuradev.backpack.client.ClientProxy;
import com.almuradev.backpack.client.network.play.C01BackpackOpenResponse;
import com.almuradev.backpack.server.BackpackDescriptor;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class S00BackpackOpenRequest implements IMessage, IMessageHandler<S00BackpackOpenRequest, C01BackpackOpenResponse> {

    public int type;
    public String title;
    public int size;

    public S00BackpackOpenRequest() {
    }

    public S00BackpackOpenRequest(int type, String title, int size) {
        this.type = type;
        this.title = title;
        this.size = size;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        type = buf.readInt();
        title = ByteBufUtils.readUTF8String(buf);
        size = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(type);
        ByteBufUtils.writeUTF8String(buf, title);
        buf.writeInt(size);
    }

    @Override
    public C01BackpackOpenResponse onMessage(S00BackpackOpenRequest message, MessageContext ctx) {
        if (ctx.side.isClient()) {
            ClientProxy.descriptor = new BackpackDescriptor(message.type, message.title, message.size);
            return new C01BackpackOpenResponse();
        }
        return null;
    }
}
