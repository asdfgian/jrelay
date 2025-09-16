package com.jrelay.ui.components.dialogs;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

import java.awt.Cursor;
import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import com.jrelay.core.models.request.Request;
import com.jrelay.core.utils.i18n.LangManager;
import com.jrelay.ui.components.dialogs.listener.SaveRequestDialogListener;
import com.jrelay.ui.components.shared.ClosableTab;
import com.jrelay.ui.components.shared.TextField;
import com.jrelay.ui.components.shared.TreeCollections;
import com.jrelay.ui.controllers.AppController;
import com.jrelay.ui.shared.styles.Colors;
import com.jrelay.ui.shared.styles.Style;
import com.jrelay.ui.shared.utils.template.Struct;

import lombok.Getter;
import net.miginfocom.swing.MigLayout;

public class SaveRequestDialog extends JPanel implements Struct {

    private final JLabel titleLabel = new JLabel();
    private final JLabel requestNameLabel = new JLabel(LangManager.text("saveRequestDialog.requestNameLabel.text"));
    @Getter
    private final TextField requestNameField = new TextField();
    private final JLabel searchLabel = new JLabel(LangManager.text("saveRequestDialog.searchLabel.text"));
    @Getter
    private final TextField searchTextField = new TextField();
    @Getter
    private final TreeCollections treeCollections = new TreeCollections(320);
    private JPanel containerButtons;
    @Getter
    private final JButton saveButton = new JButton(LangManager.text("saveRequestDialog.saveButton.text"));
    @Getter
    private final JButton cancelButton = new JButton(LangManager.text("saveRequestDialog.cancelButton.text"));

    private SaveRequestDialog() {
        this.build();
    }

    @Override
    public void initComponents() {
        containerButtons = new JPanel(new MigLayout("insets 0, gap 10", "[][]push"));
    }

    @Override
    public void configureStyle() {
        Style.setLayout(this, new MigLayout("wrap 1, insets 20", "[grow, fill]"));
        Style.setFontSize(titleLabel, 18f);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        Style.setFontSize(searchLabel, 13f);
        Style.setFontSize(requestNameLabel, 13f);
        Style.setBackgroundColor(cancelButton, Colors.SECONDARY_COLOR);
        Style.setCursor(saveButton, Cursor.HAND_CURSOR);
        Style.setCursor(cancelButton, Cursor.HAND_CURSOR);
        Style.setUndecoratedButton(cancelButton);
        searchTextField.setPlaceholder(LangManager.text("saveRequestDialog.search.placeholder.text"));
    }

    @Override
    public void attachLogic() {
        requestNameField.getTextField().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    saveButton.doClick();
                }
            }
        });
    }

    @Override
    public void compose() {
        this.add(titleLabel, "gaptop 0, gapbottom 10, align left");
        this.add(requestNameLabel, "gapbottom 0");
        this.add(requestNameField, "gapbottom 15");
        this.add(searchLabel, "gapbottom 0");
        this.add(searchTextField, "gapbottom 10");
        this.add(treeCollections, "h 220!, w 400!, gapbottom 15");
        containerButtons.add(saveButton, "h 35!");
        containerButtons.add(cancelButton, "h 35!");
        this.add(containerButtons, "gaptop 10");
    }

    public String getRequestName() {
        return requestNameField.getText();
    }

    public static void showDialog(Request request, ClosableTab closableTab) {
        SaveRequestDialog saveDialog = new SaveRequestDialog();
        saveDialog.getRequestNameField().setText(request.getName());

        JDialog dialog = new JDialog((Frame) null, true);
        dialog.setUndecorated(true);
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        dialog.getContentPane().add(saveDialog);
        dialog.pack();
        dialog.setLocationRelativeTo(null);

        saveDialog.getCancelButton().addActionListener(e -> {
            dialog.dispose();
            AppController.hideGlassPane();
        });

        SaveRequestDialogListener.setupNewCollectionButtonListener(saveDialog);
        SaveRequestDialogListener.setupSearchTextFieldListener(saveDialog);
        SaveRequestDialogListener.setupSaveButtonListener(dialog, saveDialog, request, closableTab);

        AppController.showGlassPane();
        dialog.setVisible(true);
    }
}
