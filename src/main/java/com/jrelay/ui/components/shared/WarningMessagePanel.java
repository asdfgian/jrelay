package com.jrelay.ui.components.shared;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.jetbrains.annotations.NotNull;

import com.jrelay.ui.shared.styles.Colors;
import com.jrelay.ui.shared.styles.Style;
import com.jrelay.ui.shared.utils.UiUtils;
import com.jrelay.ui.shared.utils.template.Struct;

import net.miginfocom.swing.MigLayout;

public class WarningMessagePanel extends JPanel implements Struct {
    private static WarningMessagePanel instance;
    private final JLabel icon = new JLabel(UiUtils.WARNING_ICON);
    private static final JLabel message = new JLabel("Warning");

    public static WarningMessagePanel getInstance() {
        if (instance == null) {
            instance = new WarningMessagePanel();
        }
        return instance;
    }

    public static WarningMessagePanel getInstance(@NotNull String msg) {
        message.setText(msg);
        if (instance == null) {
            instance = new WarningMessagePanel();
        }
        return instance;
    }

    private WarningMessagePanel() {
        this.build();
    }

    @Override
    public void initComponents() {
        Style.setLayout(this, new MigLayout("fillx, insets 30, aligny 50%", "[center]", "[]10[]"));
    }

    @Override
    public void configureStyle() {
        message.setHorizontalAlignment(SwingConstants.CENTER);
        Style.setFontSize(message, 20);
        Style.setTextColor(message, Colors.ICON_COLOR);
    }

    @Override
    public void compose() {
        this.add(icon, "center, wrap");
        this.add(message, "center");
    }

}
