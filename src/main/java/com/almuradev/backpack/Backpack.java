/*
 * This file is part of Backpack, licensed under the MIT License (MIT).
 *
 * Copyright (c) AlmuraDev <http://github.com/AlmuraDev>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.almuradev.backpack;

import com.almuradev.backpack.backend.DatabaseManager;
import com.google.inject.Inject;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryBasic;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.config.DefaultConfig;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.args.GenericArguments;
import org.spongepowered.api.util.command.spec.CommandSpec;

import java.io.File;
import java.nio.file.Paths;

@Plugin(id = Backpack.PLUGIN_ID, name = Backpack.PLUGIN_NAME, version = Backpack.PLUGIN_VERSION)
public class Backpack {

    public static final String PLUGIN_ID = "backpack", PLUGIN_NAME = "Backpack", PLUGIN_VERSION = "1.0";

    @Inject private Logger logger;
    @Inject
    @DefaultConfig(sharedRoot = false)
    private File configDir;

    @Listener
    public void onPreInitialization(GamePreInitializationEvent event) {
        event.getGame().getCommandDispatcher().register(this, CommandSpec.builder()
                .permission("backpack.command.open")
                .description(Texts.of("Opens your backpack"))
                .arguments(GenericArguments.playerOrSource(Texts.of("player"), event.getGame()))
                .executor((src, args) -> {
                    if (event.getGame().getPlatform().getExecutionType().isServer()) {
                        final Player player = args.<Player>getOne("player").orElse(null);
                        ((EntityPlayerMP) player).displayGUIChest(new InventoryBasic("Test", true, 54));
                    }
                    return CommandResult.success();
                })
                .build(), "backpack", "bp");
        DatabaseManager.init(Paths.get(".\\config\\backpack"), "backpacks");
    }
}
