package com.jrelay.ui.components.shared;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jrelay.ui.shared.styles.Style;
import com.jrelay.ui.shared.utils.layout.Layout;
import com.jrelay.ui.shared.utils.template.Struct;

public class NonePanel extends JPanel implements Struct {

    private final JLabel title = new JLabel("");

    public NonePanel() {
        this.build();
    }

    public NonePanel(String title) {
        this.build();
        this.title.setText(title);
    }

    @Override
    public void initComponents() {
    }

    @Override
    public void configureStyle() {
        Style.setLayout(this, Layout.tabContent());
        Style.setTransparent(this);
    }

    @Override
    public void compose() {
        this.add(title, "wrap");
    }

    public void setTitle(String title) {
        Style.setLabelText(this.title, title);
    }
}
