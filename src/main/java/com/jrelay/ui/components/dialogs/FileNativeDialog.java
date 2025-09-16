package com.jrelay.ui.components.dialogs;

import java.awt.FileDialog;
import java.io.File;

import com.jrelay.core.utils.i18n.LangManager;
import com.jrelay.ui.components.dialogs.MessageDialog.Type;
import com.jrelay.ui.views.Frame;

import kotlin.Pair;

public class FileNativeDialog {

    public enum Mode {
        LOAD, SAVE
    }

    public static Pair<String, String> show(String defaultName, Mode mode) {
        final int m = (mode == Mode.SAVE) ? FileDialog.SAVE : FileDialog.LOAD;
        final String title = mode == Mode.SAVE ? LangManager.text("fileNativeDialog.save.title.text")
                : LangManager.text("fileNativeDialog.load.title.text");
        FileDialog fileDialog = new FileDialog((Frame) null, title, m);

        if (defaultName != null && !defaultName.isBlank()) {
            fileDialog.setFile(defaultName);
        }
        fileDialog.setVisible(true);

        String directory = fileDialog.getDirectory();
        String fileName = fileDialog.getFile();

        if (mode == Mode.SAVE) {
            if (directory == null && fileName == null) {
                MessageDialog.showMessage(Type.ERROR, LangManager.text("app.fileDialog.save.canceled"));
                return null;
            }
            if (fileName == null || fileName.trim().isEmpty()) {
                MessageDialog.showMessage(Type.ERROR, LangManager.text("app.fileDialog.save.emptyName"));
                return null;
            }
        } else {
            if (directory == null || fileName == null) {
                MessageDialog.showMessage(Type.ERROR, LangManager.text("app.fileDialog.load.canceled"));
                return null;
            }
            File selected = new File(directory, fileName);
            if (!selected.exists() || !selected.isFile()) {
                MessageDialog.showMessage(Type.ERROR, LangManager.text("app.fileDialog.load.notFound"));
                return null;
            }
        }
        return new Pair<>(directory, fileName);
    }

}
