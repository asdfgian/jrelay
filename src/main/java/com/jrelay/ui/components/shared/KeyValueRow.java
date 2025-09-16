package com.jrelay.ui.components.shared;

import java.awt.Cursor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.jrelay.core.utils.i18n.LangManager;
import com.jrelay.ui.shared.styles.Colors;
import com.jrelay.ui.shared.styles.Style;
import com.jrelay.ui.shared.utils.UiUtils;
import com.jrelay.ui.shared.utils.template.Struct;
import com.jrelay.ui.shared.utils.template.Translatable;

import lombok.Getter;
import net.miginfocom.swing.MigLayout;

public class KeyValueRow extends JPanel implements Struct, Translatable {

    @Getter
    private final JCheckBox check = new JCheckBox();
    @Getter
    private TextStyleField keyField = new TextStyleField(15);
    @Getter
    private TextStyleField valueField = new TextStyleField(15);
    @Getter
    private final JButton removeRowBtn = new JButton(UiUtils.TRASH_ICON);
    private boolean isFist;

    private ObserverAutoCheck observerAutoCheck;

    public KeyValueRow(boolean isFist) {
        this.isFist = isFist;
        this.check.setSelected(false);
        this.build();
    }

    public KeyValueRow(boolean isFist, boolean selected, String key, String value) {
        this.isFist = isFist;
        this.check.setSelected(selected);
        this.keyField = new TextStyleField(key);
        this.valueField = new TextStyleField(value);
        this.build();
    }

    @Override
    public void initComponents() {
        observerAutoCheck = new ObserverAutoCheck();
    }

    @Override
    public void configureStyle() {
        Style.setLayout(this, new MigLayout("insets 2 5 2 5, fillx", "[]5[grow]5[grow]5[]"));
        Style.setBackgroundColor(this, Colors.TEXT_FIELD_COLOR);
        Style.setRoundComponent(this);
        Style.setPlaceholder(keyField, LangManager.text("keyValueRow.keyField.placeholder.text"));
        Style.setPlaceholder(valueField, LangManager.text("keyValueRow.valueField.placeholder.text"));
        Style.setTransparent(removeRowBtn);
        Style.setCursor(removeRowBtn, Cursor.HAND_CURSOR);
    }

    @Override
    public void attachLogic() {
        keyField.getDocument().addDocumentListener(observerAutoCheck);
    }

    @Override
    public void compose() {
        this.add(check);
        this.add(keyField, "h 30!, growx");
        this.add(valueField, "h 30!, growx");
        this.add(removeRowBtn);
        if (isFist)
            removeRowBtn.setVisible(false);
    }

    @Override
    public void updateText() {
        Style.setPlaceholder(keyField, LangManager.text("keyValueRow.keyField.placeholder.text"));
        Style.setPlaceholder(valueField, LangManager.text("keyValueRow.valueField.placeholder.text"));
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
