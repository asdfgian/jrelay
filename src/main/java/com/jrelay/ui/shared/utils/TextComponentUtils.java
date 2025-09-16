package com.jrelay.ui.shared.utils;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;

public class TextComponentUtils {

    public static void addDefaultContextMenu(JTextComponent textComponent) {

        UndoManager undoManager = new UndoManager();
        textComponent.getDocument().addUndoableEditListener((UndoableEditEvent e) -> {
            undoManager.addEdit(e.getEdit());
        });

        JPopupMenu menu = new JPopupMenu();

        JMenuItem undoItem = new JMenuItem("Deshacer");
        undoItem.addActionListener(e -> {
            if (undoManager.canUndo())
                undoManager.undo();
        });

        JMenuItem redoItem = new JMenuItem("Rehacer");
        redoItem.addActionListener(e -> {
            if (undoManager.canRedo())
                undoManager.redo();
        });
        JMenuItem copyItem = new JMenuItem("Copiar");
        copyItem.addActionListener(e -> textComponent.copy());

        JMenuItem pasteItem = new JMenuItem("Pegar");
        pasteItem.addActionListener(e -> textComponent.paste());

        JMenuItem cutItem = new JMenuItem("Cortar");
        cutItem.addActionListener(e -> textComponent.cut());

        menu.add(undoItem);
        menu.add(redoItem);
        menu.addSeparator();
        menu.add(copyItem);
        menu.add(pasteItem);
        menu.add(cutItem);

        textComponent.setComponentPopupMenu(menu);

        textComponent.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
        textComponent.getActionMap().put("Undo", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (undoManager.canUndo())
                    undoManager.undo();
            };
        });

        textComponent.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");
        textComponent.getActionMap().put("Redo", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (undoManager.canRedo())
                    undoManager.redo();
            };
        });
    }
}