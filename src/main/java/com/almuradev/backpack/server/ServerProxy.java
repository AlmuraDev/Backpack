/**
 * This file is part of Backpack, All Rights Reserved.
 *
 * Copyright (c) 2014 AlmuraDev <http://github.com/AlmuraDev/>
 */
package com.almuradev.backpack.server;

import com.almuradev.backpack.CommonProxy;

import java.util.concurrent.ConcurrentHashMap;

public class ServerProxy extends CommonProxy {

    public static final String CLASSPATH = "com.almuradev.backpack.server.ServerProxy";
    public static ConcurrentHashMap<String, BackpackDescriptor> DESCRIPTOR_MAP = new ConcurrentHashMap<>();
}
