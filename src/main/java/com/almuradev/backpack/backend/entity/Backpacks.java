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
package com.almuradev.backpack.backend.entity;

import com.google.common.collect.Sets;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "bp_backpacks", uniqueConstraints = {@UniqueConstraint(columnNames = {"backpackId", "world_unique_id", "player_unique_id"})})
public class Backpacks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long backpackId;

    @Column(nullable = false, name = "world_unique_id")
    private UUID worldUniqueId;

    @Column(unique = true, nullable = false, name = "player_unique_id")
    private UUID playerUniqueId;

    @Column(nullable = false, name = "title")
    private String title;

    @Column(nullable = false, name = "size")
    private int size;

    @OneToMany(mappedBy = "backpacks")
    private Set<Slots> slots = Sets.newHashSet();

    public long getBackpackId() {
        return backpackId;
    }

    public UUID getPlayerUniqueId() {
        return playerUniqueId;
    }

    public void setPlayerUniqueId(UUID playerUniqueId) {
        this.playerUniqueId = playerUniqueId;
    }

    public UUID getWorldUniqueId() {
        return worldUniqueId;
    }

    public void setWorldUniqueId(UUID worldUniqueId) {
        this.worldUniqueId = worldUniqueId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Set<Slots> getSlots() {
        return slots;
    }

    public void setSlots(Set<Slots> slots) {
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
        final Backpacks backpacks = (Backpacks) o;
        return Objects.equals(worldUniqueId, backpacks.worldUniqueId) &&
                Objects.equals(playerUniqueId, backpacks.playerUniqueId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(worldUniqueId, playerUniqueId);
    }
}
