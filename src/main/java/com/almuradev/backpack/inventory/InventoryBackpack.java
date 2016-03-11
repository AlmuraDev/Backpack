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
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class InventoryBackpack extends InventoryDatabase {

    public InventoryBackpack(Backpacks record) {
        super(record.getTitle(), record.getSize(), record);
    }

    @Override
    public Backpacks getRecord() {
        return (Backpacks) super.getRecord();
    }

    public void downgrade(Session session, CommandSource src, Player player) {
        final BackpackEvent.Resize event = new BackpackEvent.Resize(this, getRecord().getSize() - 9, Cause.of(NamedCause.source(src)));
        Sponge.getEventManager().post(event);
        if (!event.isCancelled()) {
            resize(session, this, src, player, event);
        }
    }

    public void upgrade(Session session, CommandSource src, Player player) {
        final BackpackEvent.Resize event = new BackpackEvent.Resize(this, getRecord().getSize() + 9, Cause.of(NamedCause.source(src)));
        Sponge.getEventManager().post(event);
        if (!event.isCancelled()) {
            resize(session, this, src, player, event);
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

            final InventoryBackpack resized = new InventoryBackpack(record);
            for (int i = 0; i < record.getSize(); i++) {
                resized.setInventorySlotContents(i, backpack.getStackInSlot(i));
            }

            BackpackFactory.put(resized);
            session.close();
            sendResultMessage("template.backpack.resize.success", src, player, event);
            return;
        }
        session.close();

        sendResultMessage("template.backpack.resize.failure", src, player, event);
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
        final TextTemplate template = Backpack.instance.storage.getChildNodeValue(node, TextTemplate.class);
        switch (node.toLowerCase()) {
            case "template.backpack.resize.failure":
                src.sendMessage(Backpack.instance.storage.getChildNodeValue("template.backpack.resize.failure", TextTemplate.class), ImmutableMap.of(
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
                    src.sendMessage(Backpack.instance.storage.getChildNodeValue("template.backpack.resize.success", TextTemplate.class),
                            ImmutableMap.of(
                                    "target", Text.of(TextColors.LIGHT_PURPLE, player.getName(), TextColors.RESET, "'s"),
                                    "originalSize", Text.of(event.getBackpack().getSizeInventory()),
                                    "targetSize", Text.of(event.getTargetSize())
                            ));
                }
                player.sendMessage(Backpack.instance.storage.getChildNodeValue("template.backpack.resize.success", TextTemplate.class),
                        ImmutableMap.of(
                                "target", Text.of("Your"),
                                "originalSize", Text.of(event.getBackpack().getSizeInventory()),
                                "targetSize", Text.of(event.getTargetSize())
                        ));
                break;
        }
    }
}
