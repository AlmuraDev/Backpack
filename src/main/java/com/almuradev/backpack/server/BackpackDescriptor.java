package com.almuradev.backpack.server;

public class BackpackDescriptor {

    public int type;
    public String title;
    public int size;

    public BackpackDescriptor(int type, String title, int size) {
        this.type = type;
        this.title = title;
        this.size = size;
    }
}
