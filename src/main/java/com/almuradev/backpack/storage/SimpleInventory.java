package com.almuradev.backpack.storage;

import net.minecraft.inventory.InventoryBasic;

public class SimpleInventory extends InventoryBasic {
    public SimpleInventory(String title, int slots) {
        super(title, true, slots);
    }
}
