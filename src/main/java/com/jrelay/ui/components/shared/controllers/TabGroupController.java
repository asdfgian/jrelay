package com.jrelay.ui.components.shared.controllers;

import javax.swing.JComponent;

import com.jrelay.ui.components.shared.ClosableTab;
import com.jrelay.ui.components.shared.TabGroup;

public class TabGroupController {

    private final TabGroup<?> tabGroup;

    public TabGroupController(TabGroup<?> tabGroup) {
        this.tabGroup = tabGroup;
        setupChangeListener();
        setupAddTabContent();
    }

    private void setupAddTabContent() {
        final var addTabButton = tabGroup.getAddTabButton();
        addTabButton.addActionListener(e -> {
            final int tabCount = tabGroup.getTabCount();
            final int insertIndex = tabCount - 1;
            final ClosableTab closableTab = new ClosableTab(tabGroup);

            if (insertIndex < TabGroup.TAB_LIMIT) {
                final JComponent tabContent = tabGroup.getNewInstanceTabContent(closableTab);
                tabGroup.insertTab("", null, tabContent, null, insertIndex);
                tabGroup.setTabComponentAt(insertIndex, closableTab);
                tabGroup.setSelectedIndex(insertIndex);
            }
        });
    }

    private void setupChangeListener() {
        tabGroup.addChangeListener(e -> {
            int selected = tabGroup.getSelectedIndex();
            if (selected == tabGroup.getTabCount() - 1) {
                if (tabGroup.getTabCount() > 1)
                    tabGroup.setSelectedIndex(tabGroup.getTabCount() - 2);
            }
        });
    }
}