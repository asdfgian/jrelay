package com.jrelay.ui.components.shared.controllers;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.jrelay.ui.components.shared.models.ClickableNode;

public class TreePanelEnvironmentListener {
    
    public static void setupCellButtonsListener(JTree tree) {
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                if (path == null)
                    return;
                Rectangle bounds = tree.getPathBounds(path);
                if (bounds == null)
                    return;
                int relativeX = e.getX() - bounds.x;
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                Object userObject = node.getUserObject();

                if (userObject instanceof ClickableNode clickable) {
                    clickable.handleClick(relativeX, tree);
                }
            }
        });
    }
}
