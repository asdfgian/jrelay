package com.jrelay.ui.workbech;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jrelay.core.utils.i18n.LangManager;
import com.jrelay.ui.shared.styles.Style;
import com.jrelay.ui.shared.utils.UiUtils;
import com.jrelay.ui.shared.utils.template.Struct;
import com.jrelay.ui.shared.utils.template.Translatable;

import net.miginfocom.swing.MigLayout;

public final class WorkbenchWebSocket extends JPanel implements Struct, Workbench, Translatable {
    private final JLabel icon = new JLabel(UiUtils.CONSTRUCTION_ICON);
    private final JLabel titleLabel = new JLabel();

    public WorkbenchWebSocket() {
        this.build();
        this.updateText();
        LangManager.register(this);
    }

    @Override
    public void initComponents() {
    }

    @Override
    public void configureStyle() {
        Style.setLayout(this, new MigLayout("fillx, insets 30, aligny 50%", "[center]", "[]10[]"));
        Style.setFontSize(titleLabel, 25);
    }

    @Override
    public void compose() {
        this.add(icon, "center, wrap");
        this.add(titleLabel, "wrap, center");
    }

    @Override
    public void updateText() {
        Style.setLabelText(titleLabel, LangManager.text("app.panel.construction.title.text"));
    }
}
