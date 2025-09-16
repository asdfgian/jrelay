package com.jrelay.ui.components.shared;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import com.formdev.flatlaf.FlatClientProperties;
import com.jrelay.ui.components.shared.controllers.TabGroupController;
import com.jrelay.ui.shared.styles.Colors;
import com.jrelay.ui.shared.styles.Style;
import com.jrelay.ui.shared.utils.UiUtils;
import com.jrelay.ui.shared.utils.template.Struct;

import lombok.Getter;
import net.miginfocom.swing.MigLayout;

public abstract class TabGroup<TabContent extends JComponent> extends JTabbedPane implements Struct {

    private final int BAR_HEIGHT = 48;
    public static final int TAB_LIMIT = 10;
    private final JPanel addButtonPanel = new JPanel(new MigLayout("fill", "[center]", "[center]"));
    @Getter
    private final JButton addTabButton = new JButton(UiUtils.ADD_ICON);
    private final JPanel trailingComponent = new JPanel(new MigLayout("fillx, aligny 50%", "push[]5[]5[]0"));
    private final JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
    protected final JComboBox<String> selectEnvironment = new JComboBox<>();
    protected final JButton viewerEnvVaribleButton = new JButton(UiUtils.EYE_ICON);
    @Getter
    private TabContent tabContent;

    public TabGroup(TabContent tabContent) {
        this.tabContent = tabContent;
        this.build();
        new TabGroupController(this);
    }

    @Override
    public void initComponents() {
    }

    @Override
    public void configureStyle() {
        this.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        this.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_INSETS, new Insets(0, 0, 0, 0));
        Style.setBackgroundColor(addTabButton, Colors.SECONDARY_COLOR);
        Style.setBackgroundColor(addButtonPanel, Colors.SECONDARY_COLOR);
        addButtonPanel.setPreferredSize(new Dimension(BAR_HEIGHT, BAR_HEIGHT));
        Style.setUndecoratedButton(addTabButton);
        Style.setCursor(addTabButton, Cursor.HAND_CURSOR);
        Style.setBackgroundColor(trailingComponent, Colors.SECONDARY_COLOR);
        Style.setBackgroundColor(separator, Colors.SECONDARY_COLOR);
        Style.setUndecoratedButton(viewerEnvVaribleButton);
        Style.setBackgroundColor(viewerEnvVaribleButton, Colors.SECONDARY_COLOR);
        Style.setCursor(viewerEnvVaribleButton, Cursor.HAND_CURSOR);
        Style.setFontSize(selectEnvironment, 13.5f);
        selectEnvironment.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
                        cellHasFocus);
                label.setIcon(UiUtils.STACK_ICON);
                Style.setFontSize(label, 11.5f);
                label.setBorder(BorderFactory.createEmptyBorder(7, 0, 7, 0));
                label.setText(value != null ? value.toString() : "");
                label.setHorizontalTextPosition(SwingConstants.RIGHT);
                label.setIconTextGap(10);
                return label;
            }
        });
    }

    @Override
    public void compose() {
        trailingComponent.add(separator, "h " + BAR_HEIGHT + "!");
        trailingComponent.add(selectEnvironment, "w 190!");
        trailingComponent.add(viewerEnvVaribleButton, "w 45!, h 45!");
        this.putClientProperty("JTabbedPane.trailingComponent", trailingComponent);
        addButtonPanel.add(addTabButton, "w 30!, h 30!");
        this.addTab("", (Component) tabContent);
        this.setTabComponentAt(0, new ClosableTab(this));
        this.addTab("", new JPanel());
        this.setTabComponentAt(1, addButtonPanel);
    }

    public abstract TabContent getSelectedTabContent();

    public abstract TabContent getNewInstanceTabContent(ClosableTab closableTab);

    public List<ClosableTab> getAllClosableTabs() {
        return IntStream.range(0, getTabCount() - 1)
                .mapToObj(this::getTabComponentAt)
                .map(comp -> (ClosableTab) comp)
                .collect(Collectors.toList());
    }

    public ClosableTab getSelectedClosableTab() {
        return (ClosableTab) getTabComponentAt(getSelectedIndex());
    };

    protected void loadEnvironments(String[] envs) {
        String[] items = new String[envs.length + 1];
        items[0] = "No Environment";
        System.arraycopy(envs, 0, items, 1, envs.length);
        selectEnvironment.setModel(new DefaultComboBoxModel<>(items));
    }

}
