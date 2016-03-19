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
package com.almuradev.backpack.inventory;

import com.almuradev.backpack.Backpack;
import com.almuradev.backpack.BackpackFactory;
import com.almuradev.backpack.api.database.entity.BackpackEntity;
import com.almuradev.backpack.api.event.BackpackEvent;
import com.almuradev.backpack.api.inventory.Sizes;
import com.almuradev.backpack.database.entity.Backpacks;
import com.google.common.collect.ImmutableMap;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.Optional;

public class InventoryBackpack extends InventoryDatabase {

    public InventoryBackpack(BackpackEntity record) {
        super(record.getTitle(), record.getSize(), record);
    }

    @Override
    public Backpacks getRecord() {
        return (Backpacks) super.getRecord();
    }

    public void downgrade(Session session, CommandSource src, Player player) {
        final int targetSize = getRecord().getSize() - 9;
        final BackpackEvent.Resize event = new BackpackEvent.Resize(this, targetSize, Cause.of(NamedCause.source(src)));
        Sponge.getEventManager().post(event);
        if (!event.isCancelled()) {
            resize(session, this, src, player, event);
        }
    }

    public void upgrade(Session session, CommandSource src, Player player) {
        final int targetSize = getRecord().getSize() + 9;
        if (src == player) {
            final BigDecimal cost = BigDecimal.valueOf(Backpack.instance.stash.getChildNode("worlds." + player.getWorld().getName().toLowerCase() +
                    ".economy." + Sizes.get(targetSize).name().toLowerCase()).getDouble());
            final Optional<BigDecimal> optBalance = Backpack.instance.economy.getBalance(player);
            if (optBalance.isPresent()) {
                if (cost.compareTo(optBalance.get()) >= 0) {
                    final ResultType result = Backpack.instance.economy.charge(player, cost);
                    switch (result) {
                        case SUCCESS:
                            player.sendMessage(Text.of("You were charged " + cost + " for this upgrade."));
                            break;
                        case ACCOUNT_NO_FUNDS:
                            player.sendMessage(Text.of("Insufficient funds."));
                            return;
                        default:
                            break;
                    }
                }
            }

            final BackpackEvent.Resize event = new BackpackEvent.Resize(this, targetSize, Cause.of(NamedCause.source(src)));
            Sponge.getEventManager().post(event);
            if (!event.isCancelled()) {
                resize(session, this, src, player, event);
            }
        }
    }

    private void resize(Session session, InventoryBackpack backpack, CommandSource src, Player player, BackpackEvent.Resize event) {
        if (event.getTargetSize() < getLimitSize(src, false) | event.getTargetSize() > getLimitSize(src, true)) {
            session.close();
            sendResultMessage("template.backpack.resize.limit", src, player, event);
            return;
        }

        final Backpacks record = (Backpacks) session.createCriteria(Backpacks.class).add(Restrictions.eq("backpackId", backpack.getRecord()
                .getId())).uniqueResult();
        if (record != null) {
            record.setSize(event.getTargetSize());
            session.beginTransaction();
            session.saveOrUpdate(record);
            session.getTransaction().commit();

            final BackpackEvent.Save onSaveEvent = new BackpackEvent.Save(new InventoryBackpack(record), Cause.of(NamedCause.source(src)));

            for (int i = 0; i < record.getSize(); i++) {
                onSaveEvent.getInventory().setInventorySlotContents(i, backpack.getStackInSlot(i));
            }
            Sponge.getEventManager().post(onSaveEvent);

            BackpackFactory.put((InventoryBackpack) onSaveEvent.getInventory());
            session.close();
            sendResultMessage("template.backpack.resize.success", src, player, event);
            return;
        }
        session.close();

        sendResultMessage("template.backpack.resize.failure", src, player, event);
    }

    public static int getDefaultSize(Player player) {
        for (Sizes size : Sizes.values()) {
            if (player.hasPermission("backpack." + player.getWorld().getName().toLowerCase() + ".size.default." + size.value)) {
                return size.value;
            }
        }
        return 9;
    }

    public static int getLimitSize(CommandSource src, boolean max) {
        final int defaultSize = max ? 54 : 9;
        Optional<Sizes> optSize = Optional.empty();
        if (src instanceof Player) {
            final Player player = (Player) src;
            final String rootNode = "backpack." + player.getWorld().getName().toLowerCase() + ".size." + (max ? "max" : "min");
            for (Sizes size : Sizes.values()) {
                if (player.hasPermission(rootNode + size.value)) {
                    if (optSize.isPresent()) {
                        if (max ? (size.value > optSize.get().value) : (size.value < optSize.get().value)) {
                            optSize = Optional.of(size);
                        }
                    } else {
                        optSize = Optional.of(size);
                    }
                }
            }
        }
        return optSize.isPresent() ? optSize.get().value : defaultSize;
    }

    private static void sendResultMessage(String node, CommandSource src, Player player, BackpackEvent.Resize event) {
        final TextTemplate template = Backpack.instance.stash.getChildNodeValue(node, TextTemplate.class);
        switch (node.toLowerCase()) {
            case "template.backpack.resize.failure":
                src.sendMessage(Backpack.instance.stash.getChildNodeValue("template.backpack.resize.failure", TextTemplate.class), ImmutableMap.of(
                        "target", src == player ? Text.of("Your") : Text.of(TextColors.LIGHT_PURPLE, player.getName(), TextColors.RESET, "'s")
                ));
                break;
            case "template.backpack.resize.limit":
                src.sendMessage(template, ImmutableMap.of(
                        "target", src == player ? Text.of("Your") : Text.of(TextColors.LIGHT_PURPLE, player.getName(), TextColors.RESET, "'s"),
                        "min", Text.of(getLimitSize(src, false)),
                        "max", Text.of(getLimitSize(src, true))
                ));
                break;
            case "template.backpack.resize.success":
                if (src != player) {
                    src.sendMessage(Backpack.instance.stash.getChildNodeValue("template.backpack.resize.success", TextTemplate.class),
                            ImmutableMap.of(
                                    "target", Text.of(TextColors.LIGHT_PURPLE, player.getName(), TextColors.RESET, "'s"),
                                    "originalSize", Text.of(event.getInventory().getSizeInventory()),
                                    "targetSize", Text.of(event.getTargetSize())
                            ));
                }
                player.sendMessage(Backpack.instance.stash.getChildNodeValue("template.backpack.resize.success", TextTemplate.class),
                        ImmutableMap.of(
                                "target", Text.of("Your"),
                                "originalSize", Text.of(event.getInventory().getSizeInventory()),
                                "targetSize", Text.of(event.getTargetSize())
                        ));
                break;
        }
    }
}
