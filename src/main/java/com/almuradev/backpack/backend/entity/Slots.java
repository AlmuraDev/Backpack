package com.almuradev.backpack.backend.entity;

import java.sql.Clob;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "bp_slots", uniqueConstraints = {@UniqueConstraint(columnNames = {"slotId"})})
public class Slots {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long slotId;

    @ManyToOne(targetEntity = Backpacks.class)
    @JoinColumn(name = "backpackId")
    private Backpacks backpacks;

    @Column(nullable = false, name = "slotIndex")
    private int slot;

    @Column(nullable = false, name = "data")
    private Clob data;

    public Backpacks getBackpacks() {
        return backpacks;
    }

    public void setBackpacks(Backpacks backpacks) {
        this.backpacks = backpacks;
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
