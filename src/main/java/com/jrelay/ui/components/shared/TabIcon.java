package com.jrelay.ui.components.shared;

import java.awt.Cursor;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jrelay.ui.shared.styles.Style;
import com.jrelay.ui.shared.utils.template.Struct;

import net.miginfocom.swing.MigLayout;

public class TabIcon extends JPanel implements Struct {

    private int width = 31;
    private int height = 40;

    private JLabel icon;

    private final Icon iconSVG;

    public TabIcon(Icon icon) {
        this.iconSVG = icon;
        this.build();
    }

    public TabIcon(Icon icon, int width) {
        this.iconSVG = icon;
        this.width = width;
        this.build();
    }

    @Override
    public void initComponents() {
        icon = new JLabel(iconSVG);
    }

    @Override
    public void configureStyle() {
        Style.setLayout(this, new MigLayout("fill, insets 0"));
        Style.setTransparent(this);
        Style.setCursor(this, Cursor.HAND_CURSOR);
    }

    @Override
    public void compose() {
        this.add(icon, "w " + width + "!, h " + height + "!, grow");
    }

}