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
import com.almuradev.backpack.backend.entity.Backpacks;
import com.google.inject.Inject;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import org.hibernate.Session;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Optional;

@Plugin(id = Backpack.PLUGIN_ID, name = Backpack.PLUGIN_NAME, version = Backpack.PLUGIN_VERSION)
public class Backpack {

    public static final String PLUGIN_ID = "backpack", PLUGIN_NAME = "Backpack", PLUGIN_VERSION = "1.0";
    public static Backpack instance;

    @DefaultConfig(sharedRoot = false)
    @Inject private File configDir;

    @Listener
    public void onPreInitialization(GamePreInitializationEvent event) {
        instance = this;
        Sponge.getGame().getCommandManager().register(this, CommandSpec.builder()
                .permission("backpack.command.open")
                .description(Text.of("Opens your backpack"))
                .arguments(GenericArguments.playerOrSource(Text.of("player")))
                .executor((src, args) -> {
                    if (Sponge.getGame().getPlatform().getExecutionType().isServer()) {
                        final Player player = args.<Player>getOne("player").orElse(null);
                        final Optional<BackpackInventory> optBackpackInventory = BackpackFactory.get(player.getWorld(), player);
                        if (optBackpackInventory.isPresent()) {
                            ((EntityPlayerMP) player).displayGUIChest(optBackpackInventory.get());
                        }
                    }
                    return CommandResult.success();
                })
                .child(CommandSpec.builder()
                        .permission("backpack.command.upgrade")
                        .description(Text.of("Upgrades your backpack"))
                        .arguments(GenericArguments.playerOrSource(Text.of("player")))
                        .executor((src, args) -> {
                            final Player player = args.<Player>getOne("player").orElse(null);
                            final Optional<BackpackInventory> optBackpackInventory = BackpackFactory.get(player.getWorld(), player);
                            if (optBackpackInventory.isPresent()) {
                                boolean upgraded = DatabaseManager.upgrade(DatabaseManager.getSessionFactory().openSession(), optBackpackInventory.get());
                                player.sendMessage(Text.of("Your backpack was upgraded!"));
                                if (src != player && upgraded) {
                                    src.sendMessage(Text.of(player.getName() + "'s backpack was upgraded."));
                                }
                            }
                            return CommandResult.success();
                        })
                        .build(), "upgrade", "up")
                .child(CommandSpec.builder()
                        .permission("backpack.command.view")
                        .description(Text.of("Lets you view another backpack"))
                        .arguments(GenericArguments.string(Text.of("player")))
                        .executor((src, args) -> {
                            if (!(src instanceof Player)) {
                                throw new CommandException(Text.of(TextColors.RED, "Must be in-game to view another backpack!"));
                            }
                            final String playerOrUser = args.<String>getOne("player").orElse(null);
                            final Player player = Sponge.getServer().getPlayer(playerOrUser).orElse(null);
                            if (player == null) {
                                // TODO Handle User objects
                            } else {
                                final Optional<BackpackInventory> optBackpackInventory = BackpackFactory.get(player.getWorld(), player);
                                if (optBackpackInventory.isPresent()) {
                                    ((EntityPlayerMP) src).displayGUIChest(optBackpackInventory.get());
                                }
                            }
                            return CommandResult.success();
                        })
                        .build(), "view", "vw")
                .build(), "backpack", "bp");
        DatabaseManager.init(Paths.get(".\\config\\backpack"), "backpacks");
    }

    @Listener
    public void onClientConnectionEventJoin(ClientConnectionEvent.Join event) throws IOException, SQLException {
        BackpackFactory.load(event.getTargetEntity().getWorld(), event.getTargetEntity());
    }

    @Listener
    public void onInteractInventoryEventClose(InteractInventoryEvent.Close event) throws IOException, SQLException {
        final Container container = (Container) event.getTargetInventory();

        if (container instanceof ContainerChest) {
            final ContainerChest containerChest = (ContainerChest) container;

            if (containerChest.getLowerChestInventory() instanceof BackpackInventory) {
                final BackpackInventory inventory = (BackpackInventory) containerChest.getLowerChestInventory();
                final Backpacks record = inventory.getRecord();

                final Session session = DatabaseManager.getSessionFactory().openSession();
                session.beginTransaction();
                for (int i = 0; i < inventory.getSizeInventory(); i++) {
                    final ItemStack stack = (ItemStack) (Object) inventory.getStackInSlot(i);
                    DatabaseManager.saveSlot(session, record, i, stack == null ? null : stack.toContainer());
                }
                session.getTransaction().commit();
                session.close();
            }
        }
    }
}
