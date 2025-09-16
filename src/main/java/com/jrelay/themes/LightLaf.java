package com.jrelay.themes;

import com.formdev.flatlaf.FlatLightLaf;

public class LightLaf extends FlatLightLaf {

    public static boolean setup() {
        return setup(new LightLaf());
    }

    @Override
    public String getName() {
        return "LightLaf";
    }
}
