package com.almuradev.backpack.client.play;

import com.almuradev.backpack.storage.SimpleInventory;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class C00BackpackOpenRequest implements IMessage, IMessageHandler<C00BackpackOpenRequest, IMessage> {

    @Override
    public void fromBytes(ByteBuf byteBuf) {

    }

    @Override
    public void toBytes(ByteBuf byteBuf) {

    }

    @Override
    public IMessage onMessage(C00BackpackOpenRequest message, MessageContext ctx) {
        if (ctx.side.isServer()) {
            ctx.getServerHandler().playerEntity.displayGUIChest(new SimpleInventory("Backpack", 9));
        }
        return null;
    }
}
