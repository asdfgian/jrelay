package com.jrelay.ui.components.dialogs;

import lombok.Getter;
import net.miginfocom.swing.MigLayout;

import java.awt.Cursor;
import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import com.jrelay.core.utils.i18n.LangManager;
import com.jrelay.ui.components.dialogs.MessageDialog.Location;
import com.jrelay.ui.components.dialogs.MessageDialog.Type;
import com.jrelay.ui.components.shared.TextField;
import com.jrelay.ui.controllers.AppController;
import com.jrelay.ui.shared.styles.Colors;
import com.jrelay.ui.shared.styles.Style;
import com.jrelay.ui.shared.utils.template.Struct;

public class InputDialog extends JPanel implements Struct {

    private final JLabel titleLabel = new JLabel();
    @Getter
    private final TextField inputTextField = new TextField();
    @Getter
    private final JButton saveButton = new JButton(LangManager.text("inputDialog.saveButton.text"));
    @Getter
    private final JButton cancelButton = new JButton(LangManager.text("inputDialog.cancelButton.text"));
    private final JPanel buttons = new JPanel(new MigLayout("insets 0, gap 10", "[][]push"));

    private InputDialog(String title, String input) {
        Style.setLabelText(titleLabel, title);
        inputTextField.setText(input);
        this.build();
    }

    @Override
    public void initComponents() {
        //
    }

    @Override
    public void configureStyle() {
        Style.setLayout(this, new MigLayout("wrap 1, insets 20", "[grow, fill]"));
        Style.setFontSize(titleLabel, 18f);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        Style.setCursor(saveButton, Cursor.HAND_CURSOR);

        Style.setBackgroundColor(cancelButton, Colors.SECONDARY_COLOR);
        Style.setCursor(cancelButton, Cursor.HAND_CURSOR);
        Style.setUndecoratedButton(cancelButton);
    }

    @Override
    public void attachLogic() {
        inputTextField.getTextField().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String text = inputTextField.getTextField().getText();
                    if (text == null || text.isBlank()) {
                        MessageDialog.showMessage(
                                Location.TOP_CENTER,
                                Type.ERROR,
                                LangManager.text("inputDialog.messageDialog.message.text"));
                    } else {
                        saveButton.doClick();
                    }
                }
            }
        });
    }

    @Override
    public void compose() {
        this.add(titleLabel, "align center, gapbottom 10");
        this.add(inputTextField, "h 35!, w 260!, gapbottom 15");
        buttons.add(saveButton, "h 35!");
        buttons.add(cancelButton, "h 35!");
        this.add(buttons, "growx");
    }

    private static String showDialogInternal(InputDialog inputDialog) {
        JDialog dialog = new JDialog((Frame) null, true);
        dialog.setUndecorated(true);
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        dialog.getContentPane().add(inputDialog);
        dialog.pack();
        dialog.setLocationRelativeTo(null);

        final String[] result = { null };

        boolean wasGlassPaneActive = AppController.isGlassPaneActive();

        inputDialog.getSaveButton().addActionListener(e -> {
            String input = inputDialog.getInputTextField().getText().trim();
            if (input.isEmpty()) {
                MessageDialog.showMessage(Location.TOP_CENTER, Type.ERROR, "No puede quedar vacío");
                return;
            }
            result[0] = input;
            dialog.dispose();
            if (!wasGlassPaneActive) {
                AppController.hideGlassPane();
            }
        });

        inputDialog.getCancelButton().addActionListener(e -> {
            result[0] = null;
            dialog.dispose();
            if (!wasGlassPaneActive) {
                AppController.hideGlassPane();
            }
        });

        if (!wasGlassPaneActive) {
            AppController.showGlassPane();
        }

        dialog.setVisible(true);
        return result[0] != null ? result[0].trim() : null;
    }

    public static String showDialog(String title) {
        return showDialog(title, "");
    }

    public static String showDialog(String title, String input) {
        InputDialog inputDialog = new InputDialog(title, input);
        return showDialogInternal(inputDialog);
    }

}
