package com.jrelay.ui.components.shared;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.jrelay.core.utils.i18n.LangManager;
import com.jrelay.ui.shared.styles.Colors;
import com.jrelay.ui.shared.styles.Style;
import com.jrelay.ui.shared.utils.UiUtils;
import com.jrelay.ui.shared.utils.template.Struct;
import com.jrelay.ui.shared.utils.template.Translatable;

import java.awt.Cursor;

import lombok.Getter;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;

public class KeyValueFileRow extends JPanel implements Struct, Translatable {
    @Getter
    private final JCheckBox check = new JCheckBox();
    @Getter
    private TextStyleField keyField = new TextStyleField(8);
    private final JComboBox<String> type = new JComboBox<>(new String[] {
            LangManager.text("keyValueFileRow.type.item.text.text"),
            LangManager.text("keyValueFileRow.type.item.file.text")
    });
    @Getter
    private boolean typeText = true;
    @Getter
    private TextStyleField valueField = new TextStyleField(10);
    private JPanel container;
    @Getter
    private final JButton selectFileButton = new JButton(LangManager.text("keyValueFileRow.selectFileButton.text"));
    @Getter
    @Setter
    private String filePath = null;
    @Getter
    private final JLabel contentTypeLabel = new JLabel(LangManager.text("keyValueFileRow.contentTypeLabel.text"));
    @Getter
    private final JLabel fileNameLabel = new JLabel(LangManager.text("keyValueFileRow.fileNameLabel.text"));
    @Getter
    private final JButton removeRowBtn = new JButton(UiUtils.TRASH_ICON);
    private boolean isFist;
    private ObserverAutoCheck observerAutoCheck;

    public KeyValueFileRow(boolean isFist) {
        this.isFist = isFist;
        this.check.setSelected(false);
        this.build();
    }

    public KeyValueFileRow(boolean isFist, boolean selected, String key, String value) {
        this.isFist = isFist;
        this.check.setSelected(selected);
        this.keyField = new TextStyleField(key);
        this.valueField = new TextStyleField(value);
        this.build();
    }

    @Override
    public void initComponents() {
        container = new JPanel(new MigLayout("fill, insets 0, aligny 50%", "[]10[fill]10[grow, fill]"));
        observerAutoCheck = new ObserverAutoCheck();
    }

    @Override
    public void configureStyle() {
        Style.setLayout(this, new MigLayout("insets 2 5 2 5, fillx", "[]5[]5[]2[grow,fill]5[]"));
        Style.setBackgroundColor(this, Colors.TEXT_FIELD_COLOR);
        Style.setRoundComponent(this);
        Style.setPlaceholder(keyField, LangManager.text("keyValueFileRow.keyField.placeholder.text"));
        Style.setPlaceholder(valueField, LangManager.text("keyValueFileRow.valueField.placeholder.text"));
        Style.setTransparent(removeRowBtn);
        Style.setTransparent(container);
        Style.setCursor(removeRowBtn, Cursor.HAND_CURSOR);
        Style.setFontSize(selectFileButton, 13.5f);
        Style.setFontSize(type, 13f);
        Style.setCursor(selectFileButton, Cursor.HAND_CURSOR);
        Style.setBackgroundColor(selectFileButton, Colors.SECONDARY_COLOR);
        Style.setTextColor(selectFileButton, Colors.ICON_COLOR);
        Style.setUndecoratedButton(selectFileButton);
        Style.setFontSize(contentTypeLabel, 13.5f);
        Style.setFontSize(fileNameLabel, 13.5f);
    }

    @Override
    public void attachLogic() {
        type.addActionListener(e -> {
            this.remove(3);
            if (type.getSelectedIndex() == 0) {
                this.add(valueField, "h 30!, growx", 3);
                typeText = true;
            } else if (type.getSelectedIndex() == 1) {
                this.add(container, "h 30!, growx", 3);
                typeText = false;
            }
            this.revalidate();
            this.repaint();
        });
        keyField.getDocument().addDocumentListener(observerAutoCheck);
    }

    @Override
    public void compose() {
        container.add(selectFileButton, "h 25!");
        container.add(contentTypeLabel);
        container.add(fileNameLabel);
        this.add(check);
        this.add(keyField, "h 30!, growx");
        this.add(type, "h 30!");
        this.add(valueField, "h 30!, growx");
        this.add(removeRowBtn);
        if (isFist)
            removeRowBtn.setVisible(false);
    }

    @Override
    public void updateText() {
        Style.setButtonText(selectFileButton, LangManager.text("keyValueFileRow.selectFileButton.text"));
        Style.setLabelText(contentTypeLabel, LangManager.text("keyValueFileRow.contentTypeLabel.text"));
        Style.setLabelText(fileNameLabel, LangManager.text("keyValueFileRow.fileNameLabel.text"));
        Style.setPlaceholder(keyField, LangManager.text("keyValueFileRow.keyField.placeholder.text"));
        Style.setPlaceholder(valueField, LangManager.text("keyValueFileRow.valueField.placeholder.text"));
        type.setModel(new DefaultComboBoxModel<>(new String[] {
                LangManager.text("keyValueFileRow.type.item.text.text"),
                LangManager.text("keyValueFileRow.type.item.file.text")
        }));
    }

    private class ObserverAutoCheck implements DocumentListener {

        /**
         * Automatically checks the row's checkbox when the key field is edited.
         *
         * This method attaches a {@link DocumentListener} to both the key and value
         * fields of a {@link KeyValueRow}. When text is inserted, removed, or changed
         * in the key field, the checkbox is selected if the key is not empty.
         *
         * This enhances user experience by reducing the need for manual checkbox
         * toggling
         * when a parameter is actively being edited.
         * 
         * @param row the key-value row to attach the listener to
         * 
         * @author @ASDG14N
         * @since 31-07-2025
         */
        private void updateCheckBox() {
            boolean hasKey = !keyField.getText().trim().isEmpty();
            check.setSelected(hasKey);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateCheckBox();
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            updateCheckBox();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateCheckBox();
        }
    }

}
