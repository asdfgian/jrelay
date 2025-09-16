package com.jrelay.ui.shared.styles;

import lombok.Getter;

public enum Breakpoint {
    MD(768, "Medium"),
    LG(992, "Large"),
    XL(1200, "Extra Large"),
    XXL(1400, "Extra Extra Large");

    @Getter
    private final int minWidth;
    @Getter
    private final String label;

    Breakpoint(int minWidth, String label) {
        this.minWidth = minWidth;
        this.label = label;
    }

    public static Breakpoint fromWidth(int width) {
        Breakpoint result = MD;
        for (Breakpoint bp : values()) {
            if (bp.minWidth >= 0 && width >= bp.minWidth) {
                result = bp;
            }
        }
        return result;
    }
}
