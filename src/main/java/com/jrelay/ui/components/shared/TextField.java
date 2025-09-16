package com.jrelay.ui.components.shared;

import java.awt.Color;
import java.util.function.Consumer;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentListener;

import com.jrelay.ui.shared.styles.Colors;
import com.jrelay.ui.shared.styles.Style;
import com.jrelay.ui.shared.utils.TextComponentUtils;
import com.jrelay.ui.shared.utils.template.Struct;

import javax.swing.event.DocumentEvent;

import lombok.Getter;
import net.miginfocom.swing.MigLayout;

public class TextField extends JPanel implements Struct {

    @Getter
    private final JTextField textField = new JTextField();

    public TextField() {
        this.build();
    }

    @Override
    public void initComponents() {
        TextComponentUtils.addDefaultContextMenu(textField);
    }

    @Override
    public void configureStyle() {
        Style.setLayout(this, new MigLayout("fill, insets 5, align 50% 50%"));
        Style.setRoundComponent(this);
        Style.setBackgroundColor(this, Colors.TEXT_FIELD_COLOR);
    }

    @Override
    public void compose() {
        this.add(textField, "grow");
    }

    public String getText() {
        return textField.getText();
    }

    public void setText(String text) {
        textField.setText(text);
    }

    public void setBackgroundColor(Color color) {
        Style.setBackgroundColor(textField, color);
        Style.setBackgroundColor(this, color);
    }

    public void setPlaceholder(String placeholder) {
        Style.setPlaceholder(textField, placeholder);
    }

    public void onChange(Consumer<String> listener) {
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                listener.accept(textField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                listener.accept(textField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
    }

}
