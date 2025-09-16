package com.jrelay.ui.components.shared.models;

import javax.swing.JComponent;

import com.jrelay.core.utils.i18n.LangManager;
import com.jrelay.ui.components.dialogs.MessageDialog;
import com.jrelay.ui.components.dialogs.MessageDialog.Type;
import com.jrelay.ui.controllers.AppController;

public record RequestHistoryData(String id, String idCollection, String method, String name, String timestamp)
        implements ClickableNode {
    @Override
    public void handleClick(int relativeX, JComponent jComponent) {
        if (relativeX >= 233 && relativeX <= 243) { // delete
            AppController.requestHistoryController.delete(timestamp);
            MessageDialog.showMessage(
                    Type.SUCCESS,
                    LangManager.text("app.messageDialog.deleted.text"));
        }
    }
}