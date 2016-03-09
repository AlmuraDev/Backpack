package com.almuradev.backpack.inventory;

public enum Sizes {
    TINY(9),
    SMALL(18),
    MEDIUM(27),
    LARGE(36),
    HUGE(45),
    GIGANTIC(54);

    public final int value;
    Sizes(int value) {
        this.value = value;
    }

    public static Sizes get(int size) {
        switch(size) {
            case 18:
                return SMALL;
            case 27:
                return MEDIUM;
            case 36:
                return LARGE;
            case 45:
                return HUGE;
            case 54:
                return GIGANTIC;
            default:
                return TINY;
        }
    }
}
