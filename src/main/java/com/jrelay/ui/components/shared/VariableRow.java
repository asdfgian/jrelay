package com.jrelay.ui.components.shared;

import java.awt.Cursor;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.jrelay.core.utils.i18n.LangManager;
import com.jrelay.ui.shared.styles.Colors;
import com.jrelay.ui.shared.styles.Style;
import com.jrelay.ui.shared.utils.UiUtils;
import com.jrelay.ui.shared.utils.template.Struct;

import lombok.Getter;
import net.miginfocom.swing.MigLayout;

public class VariableRow extends JPanel implements Struct {
    @Getter
    private final JCheckBox check = new JCheckBox();
    @Getter
    private JTextField variableTextField = new JTextField(8);
    @Getter
    private final JComboBox<String> type = new JComboBox<>(new String[] {
            LangManager.text("variableRow.type.item1.text"),
            LangManager.text("variableRow.type.item2.text")
    });
    @Getter
    private JTextField initialValueTextField = new JTextField(15);
    @Getter
    private JTextField currentValueTextField = new JTextField(15);
    @Getter
    private final JButton removeRowBtn = new JButton(UiUtils.TRASH_ICON);
    private boolean isFist;

    public VariableRow(boolean isFist) {
        this.isFist = isFist;
        this.check.setSelected(false);
        this.build();
    }

    @Override
    public void initComponents() {
    }

    @Override
    public void configureStyle() {
        Style.setLayout(this, new MigLayout("insets 2 5 2 5, fillx", "[]0[]2[]2[]2[]"));
        Style.setBackgroundColor(this, Colors.TEXT_FIELD_COLOR);
        Style.setRoundComponent(this);
        Style.setPlaceholder(variableTextField, LangManager.text("variableRow.variableTextField.placeholder.text"));
        Style.setFontSize(variableTextField, 13f);
        Style.setFontSize(type, 13f);
        Style.setPlaceholder(initialValueTextField,
                LangManager.text("variableRow.initialValueTextField.placeholder.text"));
        Style.setFontSize(initialValueTextField, 13f);
        Style.setPlaceholder(currentValueTextField,
                LangManager.text("variableRow.currentValueTextField.placeholder.text"));
        Style.setFontSize(currentValueTextField, 13f);
        Style.setTransparent(removeRowBtn);
        Style.setCursor(removeRowBtn, Cursor.HAND_CURSOR);
    }

    @Override
    public void attachLogic() {
        type.addActionListener(e -> {
            boolean isSecret = type.getSelectedIndex() == 1;

            String initialValue = initialValueTextField.getText();
            String currentValue = currentValueTextField.getText();

            this.remove(initialValueTextField);
            this.remove(currentValueTextField);

            if (isSecret) {
                initialValueTextField = new JPasswordField(initialValue, 15);
                currentValueTextField = new JPasswordField(currentValue, 15);
                initialValueTextField.getDocument().addDocumentListener(new DocumentListener() {
                    private void sync() {
                        currentValueTextField.setText(initialValueTextField.getText());
                    }

                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        sync();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        sync();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        sync();
                    }
                });
            } else {
                initialValueTextField = new JTextField(initialValue, 15);
                currentValueTextField = new JTextField(currentValue, 15);
                initialValueTextField.getDocument().addDocumentListener(new DocumentListener() {
                    private void sync() {
                        currentValueTextField.setText(initialValueTextField.getText());
                    }

                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        sync();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        sync();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        sync();
                    }
                });
            }

            Style.setPlaceholder(initialValueTextField,
                    LangManager.text("variableRow.initialValueTextField.placeholder.text"));
            Style.setFontSize(initialValueTextField, 13f);
            Style.setPlaceholder(currentValueTextField,
                    LangManager.text("variableRow.currentValueTextField.placeholder.text"));
            Style.setFontSize(currentValueTextField, 13f);

            this.add(initialValueTextField, "h 30!, growx", 3);
            this.add(currentValueTextField, "h 30!, growx", 4);

            this.revalidate();
            this.repaint();
        });
        initialValueTextField.getDocument().addDocumentListener(new DocumentListener() {
            private void sync() {
                currentValueTextField.setText(initialValueTextField.getText());
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                sync();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                sync();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                sync();
            }
        });

    }

    @Override
    public void compose() {
        this.add(check);
        this.add(variableTextField, "h 30!, growx");
        this.add(type, "h 30!, w 130!");
        this.add(initialValueTextField, "h 30!, growx");
        this.add(currentValueTextField, "h 30!, growx");
        this.add(removeRowBtn);
        if (isFist)
            removeRowBtn.setVisible(false);
    }

}
