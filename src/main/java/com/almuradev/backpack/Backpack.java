/**
 * This file is part of Backpack, All Rights Reserved.
 *
 * Copyright (c) 2014 AlmuraDev <http://github.com/AlmuraDev/>
 */
package com.almuradev.backpack;

import com.almuradev.backpack.client.ClientProxy;
import com.almuradev.backpack.server.ServerProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Backpack.MOD_ID)
public class Backpack {

    public static final String MOD_ID = "backpack";
    public static final Logger LOGGER = LogManager.getLogger(Backpack.class);
    public static final SimpleNetworkWrapper NETWORK_FORGE = new SimpleNetworkWrapper("BK|FOR");

    @Instance
    public static Backpack INSTANCE;

    @SidedProxy(clientSide = ClientProxy.CLASSPATH, serverSide = ServerProxy.CLASSPATH)
    public static CommonProxy PROXY;

    @EventHandler
    public void onPreInitialization(FMLPreInitializationEvent event) {
        PROXY.onPreInitialization(event);
    }
}
