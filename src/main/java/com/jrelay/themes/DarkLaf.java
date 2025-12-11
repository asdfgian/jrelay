package com.jrelay.themes;

import com.formdev.flatlaf.FlatDarkLaf;

public class DarkLaf extends FlatDarkLaf {
    public static boolean setup() {
        return setup(new DarkLaf());
    }

    @Override
    public String getName() {
                                                                                                                                return "DarkLaf";
    }
}
