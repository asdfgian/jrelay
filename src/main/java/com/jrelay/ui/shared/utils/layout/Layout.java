package com.jrelay.ui.shared.utils.layout;

import java.awt.LayoutManager;

import net.miginfocom.swing.MigLayout;

public interface Layout {

    static LayoutManager requestView() {
        return new MigLayout("fill, insets 10 15 10 15", "[grow]", "[]10[grow]");
    }

    static LayoutManager tabContent() {
        return new MigLayout("fillx, wrap 1, insets 10", "[grow]");
    }
}
