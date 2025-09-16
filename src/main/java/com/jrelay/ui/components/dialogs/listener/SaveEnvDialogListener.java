package com.jrelay.ui.components.dialogs.listener;

import java.util.List;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.jrelay.core.models.Environment;
import com.jrelay.core.utils.i18n.LangManager;
import com.jrelay.ui.components.dialogs.MessageDialog;
import com.jrelay.ui.components.dialogs.SaveEnvDialog;
import com.jrelay.ui.components.dialogs.MessageDialog.Location;
import com.jrelay.ui.components.dialogs.MessageDialog.Type;
import com.jrelay.ui.components.shared.VariableRow;
import com.jrelay.ui.controllers.AppController;
import com.jrelay.ui.shared.utils.mapper.Mapper;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SaveEnvDialogListener {

    public static void setupSaveButtonListener(JDialog dialog, SaveEnvDialog saveEnvDialog) {
        final var variableRowsList = saveEnvDialog.getVariableRowsList();
        final var saveButton = saveEnvDialog.getSaveButton();
        saveButton.addActionListener(e -> {
            final String envName = saveEnvDialog.getEnvName().getText().trim();

            if (isEnvNameInvalid(envName)) {
                MessageDialog.showMessage(
                        Location.TOP_CENTER,
                        Type.ERROR,
                        "EL nombre no puede se rvacio");
                return;
            }

            final var variables = Mapper.fromRowsToVariables(variableRowsList);
            Environment environment = new Environment(envName, variables);
            AppController.environmentController.save(environment);
            AppController.reloadComboBoxEnvironmentItems();
            AppController.renderNodesTreePanelEnviroment();
            closeDialogWithSuccess(dialog);
        });
    }

    public static void setupUpdateButtonListener(JDialog dialog, SaveEnvDialog saveEnvDialog, Environment environment) {
        final var variableRowsList = saveEnvDialog.getVariableRowsList();
        final var updateButton = saveEnvDialog.getSaveButton();
        updateButton.addActionListener(e -> {
            final String envName = saveEnvDialog.getEnvName().getText().trim();

            if (isEnvNameInvalid(envName)) {
                MessageDialog.showMessage(
                        Location.TOP_CENTER,
                        Type.ERROR,
                        "EL nombre no puede se rvacio");
                return;
            }

            final var variables = Mapper.fromRowsToVariables(variableRowsList);
            environment.setName(envName);
            environment.setVariables(variables);

            AppController.environmentController.update(environment);
            AppController.reloadComboBoxEnvironmentItems();
            AppController.renderNodesTreePanelEnviroment();
            closeDialogWithSuccess(dialog);
        });
    }

    private static boolean isEnvNameInvalid(String envName) {
        return envName == null || envName.isBlank();
    }

    private static void closeDialogWithSuccess(JDialog dialog) {
        dialog.dispose();
        AppController.hideGlassPane();
        // renderizar sidebar
        MessageDialog.showMessage(Type.SUCCESS, LangManager.text("app.messageDialog.saved.text"));
    }

    public static void setupLodadedVariableRowListener(SaveEnvDialog saveEnvDialog, Environment environment) {
        final JPanel container = saveEnvDialog.getVariableContainer();
        List<VariableRow> variableList = saveEnvDialog.getVariableRowsList();
        List<VariableRow> loadedaVariableList = Mapper.fromVariablesToRows(environment.getVariables());
        final VariableRow firstVariable = saveEnvDialog.getFirstVariableRow();

        if (!loadedaVariableList.isEmpty()) {
            firstVariable.getCheck().setSelected(loadedaVariableList.get(0).getCheck().isSelected());
            firstVariable.getVariableTextField().setText(loadedaVariableList.get(0).getVariableTextField().getText());
            firstVariable.getType().setSelectedIndex(loadedaVariableList.get(0).getType().getSelectedIndex());
            firstVariable.getInitialValueTextField()
                    .setText(loadedaVariableList.get(0).getInitialValueTextField().getText());
            firstVariable.getCurrentValueTextField()
                    .setText(loadedaVariableList.get(0).getCurrentValueTextField().getText());

            variableList.add(firstVariable);
            setupVariableCheckBoxListener(firstVariable);

            for (int i = 1; i < loadedaVariableList.size(); i++) {
                addLoadedVariableRowTo(container, loadedaVariableList.get(i), variableList);
            }
        } else {
            variableList.add(firstVariable);
            setupVariableCheckBoxListener(firstVariable);
        }

    }

    private static void addLoadedVariableRowTo(JPanel container, VariableRow row, List<VariableRow> ls) {
        setupVariableCheckBoxListener(row);
        setupKeyValueKeyReleasedListener(container, row, ls);
        setupVariableRowRemoveButtonListener(container, row, ls);
        container.add(row, "growx");
        ls.add(row);
        container.revalidate();
    }

    public static void setupVariableRowListener(SaveEnvDialog saveEnvDialog) {
        final var container = saveEnvDialog.getVariableContainer();
        final var variableRowsList = saveEnvDialog.getVariableRowsList();
        final var firstVariableRow = saveEnvDialog.getFirstVariableRow();
        variableRowsList.add(firstVariableRow);
        setupVariableCheckBoxListener(firstVariableRow);
        setupKeyValueKeyReleasedListener(container, firstVariableRow, variableRowsList);
    }

    private static void setupKeyValueKeyReleasedListener(
            JPanel container,
            VariableRow row,
            List<VariableRow> ls) {
        row.getVariableTextField().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                boolean isLast = ls.indexOf(row) == ls.size() - 1;
                boolean hasText = !row.getVariableTextField().getText().trim().isEmpty();

                if (isLast && hasText) {
                    addKeyValueRowTo(container, ls);
                }
            }
        });
    }

    private static void addKeyValueRowTo(
            JPanel container,
            List<VariableRow> ls) {
        final var row = new VariableRow(false);
        setupVariableCheckBoxListener(row);
        setupKeyValueKeyReleasedListener(container, row, ls);
        setupVariableRowRemoveButtonListener(container, row, ls);
        container.add(row, "growx");
        ls.add(row);
        container.revalidate();
    }

    private static void setupVariableCheckBoxListener(VariableRow row) {
        setupAutoCheckOnEdit(row);
        final var checkBox = row.getCheck();
        checkBox.addActionListener(e -> {
            String key = row.getVariableTextField().getText().trim();
            if (checkBox.isSelected() && key.isEmpty()) {
                MessageDialog.showMessage(
                        Location.TOP_CENTER,
                        Type.ERROR,
                        LangManager.text("saveEnvDialogListener.messageDialog.message.text"));
                checkBox.setSelected(false);
                return;
            }
        });
    }

    private static void setupVariableRowRemoveButtonListener(
            JPanel container,
            VariableRow row,
            List<VariableRow> ls) {
        row.getRemoveRowBtn().addActionListener(e -> {
            container.remove(row);
            ls.remove(row);
            container.revalidate();
            container.repaint();
        });
    }

    private static void setupAutoCheckOnEdit(VariableRow row) {
        DocumentListener listener = new DocumentListener() {
            private void updateCheckBox() {
                boolean hasKey = !row.getVariableTextField().getText().trim().isEmpty();
                row.getCheck().setSelected(hasKey);
            }

            public void insertUpdate(DocumentEvent e) {
                updateCheckBox();
            }

            public void removeUpdate(DocumentEvent e) {
                updateCheckBox();
            }

            public void changedUpdate(DocumentEvent e) {
                updateCheckBox();
            }
        };

        row.getVariableTextField().getDocument().addDocumentListener(listener);
        row.getInitialValueTextField().getDocument().addDocumentListener(listener);
    }
}
