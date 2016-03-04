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

    public static void sendResultText(BackpackChangeResult result, CommandSource src, Player player) {
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
