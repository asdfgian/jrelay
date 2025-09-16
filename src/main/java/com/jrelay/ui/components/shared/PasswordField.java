package com.jrelay.ui.components.shared;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JPasswordField;

import com.jrelay.ui.shared.styles.Colors;
import com.jrelay.ui.shared.styles.Style;
import com.jrelay.ui.shared.utils.template.Struct;

import lombok.Getter;
import net.miginfocom.swing.MigLayout;

public class PasswordField extends JPanel implements Struct {

    @Getter
    private JPasswordField passwordField;

    public PasswordField() {
        this.build();
    }

    @Override
    public void initComponents() {
        passwordField = new JPasswordField();
    }

    @Override
    public void configureStyle() {
        Style.setLayout(this, new MigLayout("fill, insets 5, align 50% 50%"));
        Style.setRoundComponent(this);
        Style.setBackgroundColor(passwordField, Colors.TEXT_FIELD_COLOR);
    }

    public void setBackgroundColor(Color color) {
        Style.setBackgroundColor(passwordField, color);
        Style.setBackgroundColor(this, color);
    }

    @Override
    public void compose() {
        this.add(passwordField, "grow");
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public void setText(String text) {
        passwordField.setText(text);
    }
}
