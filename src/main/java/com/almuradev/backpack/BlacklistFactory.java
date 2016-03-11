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

import com.almuradev.backpack.database.DatabaseManager;
import com.almuradev.backpack.database.entity.Blacklists;
import com.almuradev.backpack.inventory.InventoryBlacklist;
import com.google.common.collect.Sets;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

public class BlacklistFactory {

    private static final Set<InventoryBlacklist> BLACKLISTS = Sets.newConcurrentHashSet();

    public static InventoryBlacklist load(World world) throws IOException {
        final Session session = DatabaseManager.getSessionFactory().openSession();
        final Criteria criteria = session.createCriteria(Blacklists.class);
        Blacklists record = (Blacklists) criteria.add(Restrictions.and(Restrictions.eq("worldUniqueId", world.getUniqueId()))).uniqueResult();

        if (record == null) {
            record = new Blacklists();
            record.setWorldUniqueId(world.getUniqueId());
            record.setTitle("[" + world.getName() + "] Blacklist #" + record.getPageId());
            session.beginTransaction();
            session.saveOrUpdate(record);
            session.getTransaction().commit();
        }

        final InventoryBlacklist inventory = new InventoryBlacklist(record);
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            DatabaseManager.loadSlot(session, inventory, i);
        }
        session.close();
        BLACKLISTS.add(inventory);
        return new InventoryBlacklist(record);
    }

    public static Optional<InventoryBlacklist> get(World world, Player player) {
        for (InventoryBlacklist inventory : BLACKLISTS) {
            if (world == null && inventory.getRecord().getWorldUniqueId() == null) {
                return Optional.of(inventory);
            } else if (world != null && inventory.getRecord().getWorldUniqueId().equals(world.getUniqueId())) {
                return Optional.of(inventory);
            }
        }
        return Optional.empty();
    }

    public static void put(InventoryBlacklist inventory) {
        final Iterator<InventoryBlacklist> iter = BLACKLISTS.iterator();
        while (iter.hasNext()) {
            if (iter.next().equals(inventory)) {
                iter.remove();
            }
        }
        BLACKLISTS.add(inventory);
    }
}