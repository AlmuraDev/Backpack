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

import com.almuradev.backpack.api.event.BackpackEvent;
import com.almuradev.backpack.database.DatabaseManager;
import com.almuradev.backpack.database.entity.Backpacks;
import com.almuradev.backpack.inventory.InventoryBackpack;
import com.google.common.collect.Sets;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.world.World;

import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

public class BackpackFactory {

    private static final Set<InventoryBackpack> BACKPACKS = Sets.newConcurrentHashSet();

    public static InventoryBackpack load(World world, Player player) throws IOException {
        final Session session = DatabaseManager.getSessionFactory().openSession();
        final Criteria criteria = session.createCriteria(Backpacks.class);
        Backpacks record = (Backpacks) criteria.add(Restrictions.and(Restrictions.eq("worldUniqueId", world.getUniqueId()), Restrictions.eq
                ("playerUniqueId", player.getUniqueId()))).uniqueResult();

        if (record == null) {
            final BackpackEvent.Create onCreateEvent = new BackpackEvent.Create(new Backpacks(), Cause.of(NamedCause.source(player)));
            if (!Sponge.getEventManager().post(onCreateEvent)) {
                onCreateEvent.getRecord().setWorldUniqueId(world.getUniqueId());
                onCreateEvent.getRecord().setPlayerUniqueId(player.getUniqueId());
                onCreateEvent.getRecord().setSize(InventoryBackpack.getDefaultSize(player));
                onCreateEvent.getRecord().setTitle("My Backpack");
                session.beginTransaction();
                session.saveOrUpdate(onCreateEvent.getRecord());
                session.getTransaction().commit();
            }
        }

        final BackpackEvent.Load onLoadEvent = new BackpackEvent.Load(record, Cause.of(NamedCause.source(player)));
        Sponge.getEventManager().post(onLoadEvent);

        final InventoryBackpack inventory = new InventoryBackpack(onLoadEvent.getRecord());
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            DatabaseManager.loadSlot(session, inventory, i);
        }
        session.close();

        BACKPACKS.add(inventory);
        return inventory;
    }

    public static Optional<InventoryBackpack> get(World world, Player player) {
        for (InventoryBackpack inventory : BACKPACKS) {
            if (inventory.getRecord().getWorldUniqueId().equals(world.getUniqueId()) && inventory.getRecord().getPlayerUniqueId().equals(player
                    .getUniqueId())) {
                return Optional.of(inventory);
            }
        }
        return Optional.empty();
    }

    public static void put(InventoryBackpack inventory) {
        final Iterator<InventoryBackpack> iter = BACKPACKS.iterator();
        while (iter.hasNext()) {
            if (iter.next().equals(inventory)) {
                iter.remove();
            }
        }
        BACKPACKS.add(inventory);
    }
}
