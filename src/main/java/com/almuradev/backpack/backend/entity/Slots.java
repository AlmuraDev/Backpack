package com.almuradev.backpack.backend.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "bp_slots", uniqueConstraints = {@UniqueConstraint(columnNames = {"player_unique_id", "world_unique_id", "slot"})})
public class Slots {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(nullable = false, name = "world_unique_id")
    private UUID worldUniqueId;

    @Column(nullable = false, name = "player_unique_id")
    private UUID playerUniqueId;

    @Column(nullable = false, name = "slot")
    private int slot;

    @Column(nullable = false, name = "data")
    private String data;

    public UUID getWorldUniqueId() {
        return worldUniqueId;
    }

    public void setWorldUniqueId(UUID worldUniqueId) {
        this.worldUniqueId = worldUniqueId;
    }

    public UUID getPlayerUniqueId() {
        return playerUniqueId;
    }

    public void setPlayerUniqueId(UUID playerUniqueId) {
        this.playerUniqueId = playerUniqueId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
