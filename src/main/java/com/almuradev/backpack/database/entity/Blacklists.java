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
package com.almuradev.backpack.database.entity;

import com.almuradev.backpack.api.database.entity.InventoryEntity;
import com.almuradev.backpack.api.database.entity.SlotsEntity;
import com.google.common.collect.Sets;

import java.sql.Clob;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "bp_blacklists", uniqueConstraints = {@UniqueConstraint(columnNames = {"blacklistId", "world_unique_id"})})
public class Blacklists implements InventoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long blacklistId;

    @Column(nullable = true, name = "world_unique_id")
    private UUID worldUniqueId;

    @Column(nullable = false, name = "pageId")
    private int pageId;

    @Column(nullable = false, name = "title")
    private String title;

    @OneToMany(mappedBy = "blacklists")
    private Set<Blacklists.Slots> slots = Sets.newHashSet();

    public long getId() {
        return blacklistId;
    }

    public UUID getWorldUniqueId() {
        return worldUniqueId;
    }

    public void setWorldUniqueId(UUID worldUniqueId) {
        this.worldUniqueId = worldUniqueId;
    }

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<Blacklists.Slots> getSlots() {
        return slots;
    }

    public void setSlots(Set<Blacklists.Slots> slots) {
        this.slots = slots;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Blacklists blacklists = (Blacklists) o;
        return Objects.equals(worldUniqueId, blacklists.worldUniqueId) && Objects.equals(pageId, blacklists.pageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(worldUniqueId, pageId);
    }

    @Entity
    @Table(name = "bp_blacklist_slots", uniqueConstraints = {@UniqueConstraint(columnNames = {"slotId"})})
    public static class Slots implements SlotsEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        long slotId;

        @ManyToOne(targetEntity = Blacklists.class)
        @JoinColumn(name = "blacklistId")
        private Blacklists blacklists;

        @Column(nullable = false, name = "slotIndex")
        private int slot;

        @Column(nullable = false, name = "data")
        private Clob data;

        public long getId() {
            return slotId;
        }

        public Blacklists getInventories() {
            return blacklists;
        }

        public void setInventories(InventoryEntity blacklists) {
            if (blacklists instanceof Blacklists) {
                this.blacklists = (Blacklists) blacklists;
            }
        }

        public int getSlot() {
            return slot;
        }

        public void setSlot(int slot) {
            this.slot = slot;
        }

        public Clob getData() {
            return data;
        }

        public void setData(Clob data) {
            this.data = data;
        }
    }
}
