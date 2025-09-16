package com.jrelay.ui.components.dialogs;

import java.awt.Cursor;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import com.jrelay.core.models.Environment;
import com.jrelay.core.utils.i18n.LangManager;
import com.jrelay.ui.components.dialogs.listener.SaveEnvDialogListener;
import com.jrelay.ui.components.shared.TextField;
import com.jrelay.ui.components.shared.VariableRow;
import com.jrelay.ui.controllers.AppController;
import com.jrelay.ui.shared.styles.Colors;
import com.jrelay.ui.shared.styles.Style;
import com.jrelay.ui.shared.utils.template.Struct;

import lombok.Getter;
import net.miginfocom.swing.MigLayout;

public class SaveEnvDialog extends JPanel implements Struct {

    private final JLabel titleLabel = new JLabel();
    private final JLabel nameLabel = new JLabel(LangManager.text("saveEnvDialog.nameLabel.text"));
    @Getter
    private final TextField envName = new TextField();
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private JScrollPane scrollPane;
    @Getter
    private final JPanel variableContainer = new JPanel(new MigLayout(
            "fillx, wrap 1",
            "[grow, fill]"));
    @Getter
    private final List<VariableRow> variableRowsList = new ArrayList<>();
    @Getter
    private final VariableRow firstVariableRow = new VariableRow(true);
    private final JPanel buttonsContainer = new JPanel();
    @Getter
    private final JButton saveButton = new JButton(LangManager.text("saveEnvDialog.saveButton.text"));
    private final JButton cancelButton = new JButton(LangManager.text("saveEnvDialog.cancelButton.text"));

    private SaveEnvDialog(boolean isEdit) {
        if (isEdit) {
            Style.setLabelText(titleLabel, LangManager.text("saveEnvDialog.titleLabel.editEnv.text"));
        } else {
            Style.setLabelText(titleLabel, LangManager.text("saveEnvDialog.titleLabel.text"));
        }
        this.build();
    }

    @Override
    public void initComponents() {
        scrollPane = new JScrollPane(variableContainer,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
    }

    @Override
    public void configureStyle() {
        Style.setLayout(this, new MigLayout("wrap 1, insets 20", "[grow, fill]"));
        Style.setFontSize(titleLabel, 18f);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        Style.setFontSize(nameLabel, 13f);
        Style.setTabArc(tabbedPane);
        Style.setLayout(buttonsContainer, new MigLayout("insets 0, gap 10", "[][]push"));
        Style.setBackgroundColor(cancelButton, Colors.SECONDARY_COLOR);
        Style.setCursor(saveButton, Cursor.HAND_CURSOR);
        Style.setUndecoratedButton(cancelButton);
        Style.setCursor(cancelButton, Cursor.HAND_CURSOR);
    }

    @Override
    public void compose() {
        this.add(titleLabel, "gaptop 0, gapbottom 10, align left");
        this.add(nameLabel, "gapbottom 0");
        this.add(envName, "gapbottom 5");
        variableContainer.add(firstVariableRow, "growx, pushx");
        tabbedPane.addTab(LangManager.text("saveEnvDialog.tabbedPane.tab.title.text"), scrollPane);
        this.add(tabbedPane, "h 300!, w 700!, gapbottom 5");
        buttonsContainer.add(saveButton, "h 35!");
        buttonsContainer.add(cancelButton, "h 35!");
        this.add(buttonsContainer, "gaptop 10");
    }

    private static JDialog createDialog(SaveEnvDialog saveEnvDialog) {
        JDialog dialog = new JDialog((Frame) null, true);
        dialog.setUndecorated(true);
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        dialog.getContentPane().add(saveEnvDialog);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        saveEnvDialog.cancelButton.addActionListener(e -> {
            dialog.dispose();
            AppController.hideGlassPane();
        });
        return dialog;
    }

    private static void showDialog(JDialog dialog) {
        AppController.showGlassPane();
        dialog.setVisible(true);
    }

    public static void showNewEnvDialog() {
        SaveEnvDialog saveEnvDialog = new SaveEnvDialog(false);
        final var dialog = createDialog(saveEnvDialog);
        SaveEnvDialogListener.setupVariableRowListener(saveEnvDialog);
        SaveEnvDialogListener.setupSaveButtonListener(dialog, saveEnvDialog);
        showDialog(dialog);
    }

    public static void showEditEnvDialog(Environment environment) {
        SaveEnvDialog saveEnvDialog = new SaveEnvDialog(true);
        saveEnvDialog.getEnvName().setText(environment.getName());
        SaveEnvDialogListener.setupLodadedVariableRowListener(saveEnvDialog, environment);
        final var dialog = createDialog(saveEnvDialog);
        SaveEnvDialogListener.setupUpdateButtonListener(dialog, saveEnvDialog, environment);
        showDialog(dialog);
    }

}
