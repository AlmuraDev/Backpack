package com.almuradev.backpack;

import com.almuradev.backpack.backend.entity.Backpacks;
import net.minecraft.inventory.InventoryBasic;

import java.util.Objects;

public class BackpackInventory extends InventoryBasic {
    private final Backpacks record;

    public BackpackInventory(Backpacks record) {
        super(record.getTitle(), true, record.getSize());
        this.record = record;
    }

    public Backpacks getRecord() {
        return record;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BackpackInventory inventory = (BackpackInventory) o;
        return Objects.equals(record, inventory.record);
    }

    @Override
    public int hashCode() {
        return Objects.hash(record);
    }
}
