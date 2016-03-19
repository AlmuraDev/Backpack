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
package com.almuradev.backpack.api.event;


import com.almuradev.backpack.api.database.entity.BackpackEntity;
import com.almuradev.backpack.api.inventory.IInventoryDatabase;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

public abstract class BackpackEvent extends AbstractEvent {

    public static final class Create extends BackpackEvent {

        private BackpackEntity record;
        private final Cause cause;

        public Create(BackpackEntity record, Cause cause) {
            this.record = record;
            this.cause = cause;
        }

        public BackpackEntity getRecord() {
            return record;
        }

        @Override
        public Cause getCause() {
            return cause;
        }
    }

    public static final class Load extends BackpackEvent {

        private BackpackEntity record;
        private final Cause cause;

        public Load(BackpackEntity record, Cause cause) {
            this.record = record;
            this.cause = cause;
        }

        public BackpackEntity getRecord() {
            return record;
        }

        @Override
        public Cause getCause() {
            return cause;
        }
    }

    public static final class Save extends BackpackEvent {
        private IInventoryDatabase inventory;
        private final Cause cause;

        public Save(IInventoryDatabase inventory, Cause cause) {
            this.inventory = inventory;
            this.cause = cause;
        }

        public IInventoryDatabase getInventory() {
            return inventory;
        }

        @Override
        public Cause getCause() {
            return cause;
        }
    }

    public static final class Resize extends BackpackEvent implements Cancellable {
        private IInventoryDatabase inventory;
        private int targetSize;
        private boolean cancelled;
        private final Cause cause;

        public Resize(IInventoryDatabase inventory, int targetSize, Cause cause) {
            this.inventory = inventory;
            this.targetSize = targetSize;
            this.cause = cause;
        }

        public IInventoryDatabase getInventory() {
            return inventory;
        }

        public void setInventory(IInventoryDatabase inventory) {
            this.inventory = inventory;
        }

        public int getTargetSize() {
            return targetSize;
        }

        public void setTargetSize(int targetSize) {
            this.targetSize = targetSize;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }

        @Override
        public Cause getCause() {
            return cause;
        }
    }
}
