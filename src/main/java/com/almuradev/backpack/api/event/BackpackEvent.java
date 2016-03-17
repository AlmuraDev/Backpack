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


import com.almuradev.backpack.database.entity.Backpacks;
import com.almuradev.backpack.inventory.InventoryBackpack;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

public abstract class BackpackEvent extends AbstractEvent {

     public static final class Create extends BackpackEvent implements Cancellable {

         private Backpacks record;
         private boolean cancelled = false;
         private final Cause cause;

         public Create(Backpacks record, Cause cause) {
             this.record = record;
             this.cause = cause;
         }

         public Backpacks getRecord() {
             return record;
         }

         public void setRecord(Backpacks record) {
             this.record = record;
         }

         public boolean isCancelled() {
             return cancelled;
         }

         public void setCancelled(boolean cancel) {
             this.cancelled = cancel;
         }

         @Override
         public Cause getCause() {
             return cause;
         }
     }

    public static class Load extends BackpackEvent {

        private Backpacks record;
        private final Cause cause;

        public Load(Backpacks record, Cause cause) {
            this.record = record;
            this.cause = cause;
        }

        public Backpacks getRecord() {
            return record;
        }

        public void setRecord(Backpacks record) {
            this.record = record;
        }

        @Override
        public Cause getCause() {
            return cause;
        }

        public static class Post extends Load {

            private InventoryBackpack backpack;

            public Post(InventoryBackpack backpack, Cause cause) {
                super(backpack.getRecord(), cause);
                this.backpack = backpack;
            }

            public InventoryBackpack getBackpack() {
                return backpack;
            }
        }
    }

    public static final class Resize extends BackpackEvent implements Cancellable {

        private InventoryBackpack backpack;
        private int targetSize;
        private boolean cancelled = false;
        private final Cause cause;

        public Resize(InventoryBackpack backpack, int targetSize, Cause cause) {
            this.backpack = backpack;
            this.targetSize = targetSize;
            this.cause = cause;
        }

        public InventoryBackpack getBackpack() {
            return backpack;
        }

        public void setBackpack(InventoryBackpack backpack) {
            this.backpack = backpack;
        }

        public int getTargetSize() {
            return targetSize;
        }

        public void setTargetSize(int targetSize) {
            this.targetSize = targetSize;
        }

        public boolean isCancelled() {
            return cancelled;
        }

        public void setCancelled(boolean cancel) {
            this.cancelled = cancel;
        }

        @Override
        public Cause getCause() {
            return cause;
        }
    }
}
