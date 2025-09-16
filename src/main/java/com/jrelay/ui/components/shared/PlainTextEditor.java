package com.jrelay.ui.components.shared;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.jrelay.ui.shared.styles.Colors;
import com.jrelay.ui.shared.styles.Style;
import com.jrelay.ui.shared.utils.TextComponentUtils;
import com.jrelay.ui.shared.utils.template.Struct;

import lombok.Getter;
import net.miginfocom.swing.MigLayout;

public class PlainTextEditor extends JPanel implements Struct {

    private JScrollPane scroll;
    @Getter
    private final JTextArea textArea = new JTextArea();

    public PlainTextEditor() {
        this.build();
    }

    @Override
    public void initComponents() {
        scroll = new JScrollPane(textArea);
        TextComponentUtils.addDefaultContextMenu(textArea);
    }

    @Override
    public void configureStyle() {
        Style.setLayout(this, new MigLayout("fill, insets 0"));
        Style.setBackgroundColor(this, Colors.SECONDARY_COLOR);
    }

    @Override
    public void compose() {
        this.add(scroll, "grow");
    }

    public String getText() {
        return textArea.getText();
    }

}
