package com.jrelay.ui.components.shared.models;

import javax.swing.JComponent;

public interface ClickableNode {
    void handleClick(int relativeX, JComponent jComponent);
}