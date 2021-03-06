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

    public BackpackInventory clone() {
        final BackpackInventory clone = new BackpackInventory(record);
        for (int i = 0; i < this.getSizeInventory(); i++) {
            clone.setInventorySlotContents(i, this.getStackInSlot(i));
            clone.setCustomName(this.getName());
        }
        return clone;
    }

    public InventoryBasic getReadOnly() {
        final InventoryBasic clone = new InventoryBasic(this.getName(), true, this.getSizeInventory());
        for (int i = 0; i < this.getSizeInventory(); i++) {
            clone.setInventorySlotContents(i, this.getStackInSlot(i));
        }
        return clone;
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
