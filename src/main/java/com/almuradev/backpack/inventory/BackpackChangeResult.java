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

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class BackpackChangeResult {

    public static class Success extends BackpackChangeResult {

        public int originalSize;
        public int newSize;

        public Success(int originalSize, int newSize) {
            this.originalSize = originalSize;
            this.newSize = newSize;
        }
    }

    public static class Failure extends BackpackChangeResult {

        public int targetSize;
        public int currentSize;

        public Failure(int targetSize, int currentSize) {
            this.targetSize = targetSize;
            this.currentSize = currentSize;
        }
    }

    public static class LimitReached extends BackpackChangeResult {

        public int targetSize;
        public int currentSize;

        public LimitReached(int targetSize, int currentSize) {
            this.targetSize = targetSize;
            this.currentSize = currentSize;
        }
    }

    // TODO: Update with TextTemplate to add color when branch is merged.
    public static void sendResultMessages(BackpackChangeResult result, CommandSource src, Player player) {
        if (result instanceof Success) {
            final String format = "%s backpack was %s from %d to %d.";
            if (src != player) {
                src.sendMessage(Text.of(String.format(format,
                        player.getName() + "'s",
                        ((Success) result).originalSize < ((Success) result).newSize ? "upgraded" : "downgraded",
                        ((Success) result).originalSize,
                        ((Success) result).newSize)));
            }
            player.sendMessage(Text.of(String.format(format,
                    "Your",
                    ((Success) result).originalSize < ((Success) result).newSize ? "upgraded" : "downgraded",
                    ((Success) result).originalSize,
                    ((Success) result).newSize)));

        } else if (result instanceof Failure) {
            final String format = "%s backpack was unable to be %s.";
            src.sendMessage(Text.of(String.format(format,
                    src == player ? "Your" : player.getName() + "'s",
                    ((Failure) result).currentSize < ((Failure) result).targetSize ? "upgraded" : "downgraded")));
        } else if (result instanceof LimitReached) {
            final String format = "%s backpack has already reached the %s size.";
            src.sendMessage(Text.of(String.format(format,
                    src == player ? "Your" : player.getName() + "'s",
                    ((LimitReached) result).currentSize < ((LimitReached) result).targetSize ? "maximum" : "minimum")));

        }
    }
}
