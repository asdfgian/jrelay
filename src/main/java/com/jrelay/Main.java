package com.jrelay;

import javax.swing.SwingUtilities;

import com.jrelay.ui.controllers.AppController;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            final var app = new AppController();
            app.run();
        });
    }
}