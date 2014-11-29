/**
 * This file is part of Backpack, All Rights Reserved.
 *
 * Copyright (c) 2014 AlmuraDev <http://github.com/AlmuraDev/>
 */
package com.almuradev.backpack.client;

import com.almuradev.backpack.Backpack;
import com.almuradev.backpack.CommonProxy;
import com.almuradev.backpack.server.network.play.S00BackpackOpenRequest;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class ClientProxy extends CommonProxy {
    public static final String CLASSPATH = "com.almuradev.backpack.client.ClientProxy";
    public static final KeyBinding BINDING_OPEN_BACKPACK = new KeyBinding("Open Backpack", Keyboard.KEY_B, "Opens a backpack!");

    @Override
    public void onPreInitialization(FMLPreInitializationEvent event) {
        super.onPreInitialization(event);
        FMLCommonHandler.instance().bus().register(this);
    }

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        if (BINDING_OPEN_BACKPACK.isPressed()) {
            Backpack.NETWORK_FORGE.sendToServer(new S00BackpackOpenRequest());
        }
    }
}
