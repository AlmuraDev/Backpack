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

import com.almuradev.backpack.api.database.entity.SlotsEntity;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.item.inventory.ItemStack;

public abstract class SlotEvent extends AbstractEvent {

    public static final class Load extends SlotEvent {
        private SlotsEntity record;
        private int index;
        private ItemStack itemStack;
        private final Cause cause;

        public Load(SlotsEntity record, int index, ItemStack itemStack, Cause cause) {
            this.record = record;
            this.index = index;
            this.itemStack = itemStack;
            this.cause = cause;
        }

        public SlotsEntity getRecord() {
            return record;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }

        public void setItemStack(ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        @Override
        public Cause getCause() {
            return cause;
        }
    }

    public static final class Save extends SlotEvent {
        private SlotsEntity record;
        private int index;
        private ItemStack itemStack;
        private final Cause cause;

        public Save(SlotsEntity record, int index, ItemStack itemStack, Cause cause) {
            this.record = record;
            this.index = index;
            this.itemStack = itemStack;
            this.cause = cause;
        }

        public SlotsEntity getRecord() {
            return record;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }

        public void setItemStack(ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        @Override
        public Cause getCause() {
            return cause;
        }
    }
}
