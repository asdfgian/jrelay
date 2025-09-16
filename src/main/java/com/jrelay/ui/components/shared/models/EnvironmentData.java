package com.jrelay.ui.components.shared.models;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JComponent;

import com.jrelay.core.models.Environment;
import com.jrelay.core.utils.i18n.LangManager;
import com.jrelay.ui.components.dialogs.ConfirmDialog;
import com.jrelay.ui.components.dialogs.MessageDialog;
import com.jrelay.ui.components.dialogs.SaveEnvDialog;
import com.jrelay.ui.components.dialogs.ConfirmDialog.Option;
import com.jrelay.ui.components.dialogs.FileNativeDialog;
import com.jrelay.ui.components.dialogs.FileNativeDialog.Mode;
import com.jrelay.ui.components.dialogs.MessageDialog.Type;
import com.jrelay.ui.controllers.AppController;

public record EnvironmentData(String id, String name) implements ClickableNode {

    @Override
    public void handleClick(int relativeX, JComponent jComponent) {
        if (relativeX >= 0 && relativeX < 200) {
            System.out.println(id);
        }
        if (relativeX >= 200 && relativeX <= 215) {
            final Environment environment = AppController.environmentController.find(id);
            SaveEnvDialog.showEditEnvDialog(environment);
        } else if (relativeX > 225 && relativeX <= 240) {
            final var result = FileNativeDialog.show("personal-environments", Mode.SAVE);

            if (result != null) {
                String directory = result.getFirst();
                String filename = result.getSecond();
                Path selectedPath = Paths.get(directory, filename);
                if (!filename.toLowerCase().endsWith(".json")) {
                    selectedPath = selectedPath.resolveSibling(filename + ".json");
                }
                AppController.osManager.copyFile("environment.json", selectedPath.toString());
                MessageDialog.showMessage(
                        Type.SUCCESS,
                        LangManager.text("app.messageDialog.saved.text"));
            }
        } else if (relativeX > 245 && relativeX <= 260) {
            Option option = ConfirmDialog.show(LangManager.text("confirmDialog.deleteEnv.message.text"));
            if (option == Option.YES) {
                AppController.environmentController.delete(id);
                AppController.renderNodesTreePanelEnviroment();
                AppController.reloadComboBoxEnvironmentItems();
                MessageDialog.showMessage(Type.SUCCESS, LangManager.text("app.messageDialog.deleted.text"));
            }
        }
    }

}
