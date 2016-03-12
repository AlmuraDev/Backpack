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

import static org.spongepowered.api.command.args.GenericArguments.optional;
import static org.spongepowered.api.command.args.GenericArguments.string;

import com.almuradev.backpack.api.inventory.Sizes;
import com.almuradev.backpack.database.DatabaseManager;
import com.almuradev.backpack.database.entity.Backpacks;
import com.almuradev.backpack.database.entity.Blacklists;
import com.almuradev.backpack.inventory.InventoryBackpack;
import com.almuradev.backpack.inventory.InventoryBlacklist;
import com.almuradev.backpack.util.Storage;
import com.google.inject.Inject;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Optional;

@Plugin(id = Backpack.PLUGIN_ID, name = Backpack.PLUGIN_NAME, version = Backpack.PLUGIN_VERSION)
public class Backpack {

    public static final String PLUGIN_ID = "com.almuradev.backpack", PLUGIN_NAME = "Backpack", PLUGIN_VERSION = "1.0";
    public static Backpack instance;
    public Storage storage;

    @Inject public Logger logger;
    @Inject public PluginContainer container;
    @DefaultConfig(sharedRoot = false)
    @Inject private File configuration;
    @DefaultConfig(sharedRoot = false)
    @Inject private ConfigurationLoader<CommentedConfigurationNode> loader;

    @Listener
    public void onGamePreInitializationEvent(GamePreInitializationEvent event) {
        instance = this;
        storage = new Storage(container, configuration, loader);
        storage.registerDefaultNode(Storage.DefaultNode.builder(TextTemplate.class)
                .key("template.backpack.resize.success")
                .value(TextTemplate.of(
                        TextTemplate.arg("target"),
                        " backpack was resized from ",
                        TextTemplate.arg("originalSize"),
                        " to ",
                        TextTemplate.arg("targetSize")))
                .type(Optional.of(TextTemplate.class))
                .build()
        );
        storage.registerDefaultNode(Storage.DefaultNode.builder(TextTemplate.class)
                .key("template.backpack.resize.failure")
                .value(TextTemplate.of(
                        "Unable to resize",
                        TextTemplate.arg("target"),
                        " backpack"))
                .type(Optional.of(TextTemplate.class))
                .build()
        );
        storage.registerDefaultNode(Storage.DefaultNode.builder(TextTemplate.class)
                .key("template.backpack.resize.limit")
                .value(TextTemplate.of(
                        TextTemplate.arg("target"),
                        " backpack has already reached its size limit (min: ",
                        TextTemplate.arg("min"),
                        ", max: ",
                        TextTemplate.arg("max"),
                        ")"))
                .type(Optional.of(TextTemplate.class))
                .build()
        );


        Sponge.getGame().getCommandManager().register(this, CommandSpec.builder()
                .permission("backpack.command.open")
                .description(Text.of("Opens your backpack"))
                .arguments(GenericArguments.playerOrSource(Text.of("player")))
                .executor((src, args) -> {
                    if (Sponge.getGame().getPlatform().getExecutionType().isServer()) {
                        final Player player = args.<Player>getOne("player").orElse(null);
                        final Optional<InventoryBackpack> optBackpackInventory = BackpackFactory.get(player.getWorld(), player);
                        if (optBackpackInventory.isPresent()) {
                            optBackpackInventory.get().setCustomName("My Backpack");
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
                            final Optional<InventoryBackpack> optBackpackInventory = BackpackFactory.get(player.getWorld(), player);
                            if (optBackpackInventory.isPresent()) {
                                optBackpackInventory.get().upgrade(DatabaseManager.getSessionFactory().openSession(), src, player);
                            }
                            return CommandResult.success();
                        })
                        .build(), "upgrade", "up")
                .child(CommandSpec.builder()
                        .permission("backpack.command.downgrade")
                        .description(Text.of("Downgrade your backpack"))
                        .arguments(GenericArguments.playerOrSource(Text.of("player")))
                        .executor((src, args) -> {
                            final Player player = args.<Player>getOne("player").orElse(null);
                            final Optional<InventoryBackpack> optBackpackInventory = BackpackFactory.get(player.getWorld(), player);
                            if (optBackpackInventory.isPresent()) {
                                optBackpackInventory.get().downgrade(DatabaseManager.getSessionFactory().openSession(), src, player);
                            }
                            return CommandResult.success();
                        })
                        .build(), "downgrade", "down")
                .child(CommandSpec.builder()
                        .permission("backpack.command.view")
                        .description(Text.of("Lets you view another backpack"))
                        .arguments(string(Text.of("player")))
                        .executor((src, args) -> {
                            if (!(src instanceof Player)) {
                                throw new CommandException(Text.of(TextColors.RED, "Must be in-game to view another backpack!"));
                            }
                            final String playerOrUser = args.<String>getOne("player").orElse(null);
                            final Player player = Sponge.getServer().getPlayer(playerOrUser).orElse(null);
                            if (player == null) {
                                // TODO Handle User objects
                            } else {
                                final Optional<InventoryBackpack> optBackpackInventory = BackpackFactory.get(player.getWorld(), player);
                                if (optBackpackInventory.isPresent()) {
                                    final boolean modifiable = src.hasPermission("backpack.command.view.modify");
                                    final InventoryBackpack inventory = new InventoryBackpack(optBackpackInventory.get().getRecord());
                                    inventory.setModifiable(modifiable);
                                    inventory.setCustomName(player.getName() + "'s Backpack");
                                    src.sendMessage(Text.of(String
                                            .format("Opening %s's backpack in %s mode.", player.getName(), modifiable ? "live" : "read-only")));
                                    ((EntityPlayerMP) src).displayGUIChest(inventory);
                                }
                            }
                            return CommandResult.success();
                        })
                        .build(), "view", "vw")
                .child(CommandSpec.builder()
                        .permission("backpack.command.blacklist")
                        .description(Text.of("Opens the blacklist for the world"))
                        .arguments(optional(string(Text.of("target"))))
                        .executor((src, args) -> {
                            if (!(src instanceof Player)) {
                                throw new CommandException(Text.of(TextColors.RED, "Must be in-game to view a blacklist!"));
                            }
                            final Player player = (Player) src;
                            Optional<String> optTarget = args.getOne("target");
                            Optional<InventoryBlacklist> optBlacklistInventory = Optional.empty();
                            if (optTarget.isPresent()) {
                                if (optTarget.get().equalsIgnoreCase("global")) {
                                    optBlacklistInventory = BlacklistFactory.get(Optional.empty());
                                } else {
                                    final Optional<World> optWorld = Sponge.getServer().getWorld(optTarget.get());
                                    if (optWorld.isPresent()) {
                                        optBlacklistInventory = BlacklistFactory.get(optWorld);
                                    }
                                }
                            } else {
                                optBlacklistInventory = BlacklistFactory.get(Optional.of(player.getWorld()));
                            }
                            if (optBlacklistInventory.isPresent()) {
                                final boolean modifiable = src.hasPermission("backpack.command.blacklist.modify");
                                optBlacklistInventory.get().setModifiable(modifiable);
                                src.sendMessage(Text.of(String.format("Opening %s in %s mode.", optBlacklistInventory.get().getName(),
                                        modifiable ? "live" : "read-only")));
                                ((EntityPlayerMP) src).displayGUIChest(optBlacklistInventory.get());
                            }
                            return CommandResult.success();
                        })
                        .build(), "blacklist", "bl")
                .child(CommandSpec.builder()
                        .permission("backpack.command.reload")
                        .description(Text.of("Reloads the configuration file."))
                        .executor((src, args) -> {
                            if (Sponge.getGame().getPlatform().getExecutionType().isServer()) {
                                storage.init();
                                src.sendMessage(Text.of("Backpack configuration reloaded."));
                            }
                            return CommandResult.success();
                        })
                        .build(), "reload", "rl")
                .build(), "backpack", "bp");
        DatabaseManager.init(Paths.get(".\\" + Backpack.instance.configuration.getParent()), "backpacks");
    }

    @Listener
    public void onGameInitializationEvent(GameInitializationEvent event) throws IOException {
        BlacklistFactory.load(Optional.empty());
    }

    @Listener
    public void onClientConnectionEventJoin(ClientConnectionEvent.Join event) throws IOException {
        BackpackFactory.load(event.getTargetEntity().getWorld(), event.getTargetEntity());
    }

    @Listener
    public void onLoadWorldEvent(LoadWorldEvent event) throws IOException {
        BlacklistFactory.load(Optional.of(event.getTargetWorld()));
    }

    @Listener
    public void onInteractInventoryEventClose(InteractInventoryEvent.Close event) throws IOException, SQLException {
        final Container container = (Container) event.getTargetInventory();

        if (container instanceof ContainerChest) {
            final ContainerChest containerChest = (ContainerChest) container;

            if (containerChest.getLowerChestInventory() instanceof InventoryBackpack) {
                final InventoryBackpack inventory = (InventoryBackpack) containerChest.getLowerChestInventory();
                final Backpacks record = inventory.getRecord();

                final Session session = DatabaseManager.getSessionFactory().openSession();
                session.beginTransaction();
                for (int i = 0; i < record.getSize(); i++) {
                    final ItemStack stack = (ItemStack) (Object) inventory.getStackInSlot(i);
                    DatabaseManager.saveSlot(session, record, i, stack == null ? null : stack.toContainer());
                }
                session.getTransaction().commit();
                session.close();
            } else if (containerChest.getLowerChestInventory() instanceof InventoryBlacklist) {
                final InventoryBlacklist inventory = (InventoryBlacklist) containerChest.getLowerChestInventory();
                final Blacklists record = inventory.getRecord();

                final Session session = DatabaseManager.getSessionFactory().openSession();
                session.beginTransaction();
                for (int i = 0; i < Sizes.GIGANTIC.value; i++) {
                    final ItemStack stack = (ItemStack) (Object) inventory.getStackInSlot(i);
                    DatabaseManager.saveSlot(session, record, i, stack == null ? null : stack.toContainer());
                }
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    @Listener
    public void onDestructEntityEvent(DestructEntityEvent event) {
        if (event.getTargetEntity() instanceof Player) {
            final Player player = (Player) event.getTargetEntity();
            if (!player.hasPermission("backpack." + player.getWorld().getName().toLowerCase() + ".death.bypass")) {
                final Optional<InventoryBackpack> optBackpack = BackpackFactory.get(player.getWorld(), player);
                if (optBackpack.isPresent()) {
                    for (int i = 0; i < optBackpack.get().getSizeInventory(); i++) {
                        ((EntityPlayerMP) player).dropItem(optBackpack.get().getStackInSlot(i), true, true);
                        optBackpack.get().setInventorySlotContents(i, null);
                    }
                }
            }
        }
    }
}
