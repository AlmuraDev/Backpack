/**
 * This file is part of Backpack, All Rights Reserved.
 *
 * Copyright (c) 2014 AlmuraDev <http://github.com/AlmuraDev/>
 */
package com.almuradev.backpack;

import com.almuradev.backpack.client.play.C00BackpackOpenRequest;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy {
    public void onPreInitialization(FMLPreInitializationEvent event) {
        Backpack.NETWORK_FORGE.registerMessage(C00BackpackOpenRequest.class, C00BackpackOpenRequest.class, 0, Side.SERVER);
    }
}
