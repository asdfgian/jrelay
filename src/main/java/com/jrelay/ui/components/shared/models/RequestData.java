package com.jrelay.ui.components.shared.models;

import javax.swing.JComponent;

import com.jrelay.core.models.request.Request;
import com.jrelay.core.utils.i18n.LangManager;
import com.jrelay.ui.components.dialogs.ConfirmDialog;
import com.jrelay.ui.components.dialogs.InputDialog;
import com.jrelay.ui.components.dialogs.MessageDialog;
import com.jrelay.ui.components.dialogs.ConfirmDialog.Option;
import com.jrelay.ui.components.dialogs.MessageDialog.Type;
import com.jrelay.ui.components.shared.TreeCollections;
import com.jrelay.ui.controllers.AppController;

public record RequestData(String id, String idCollection, String method, String name) implements ClickableNode {
    @Override
    public void handleClick(int relativeX, JComponent jComponent) {
        final int WIDTH = ((TreeCollections) jComponent).getWidthCell();
        if (WIDTH < 300) {
            if (relativeX >= 0 && relativeX <= 205) {
                final Request request = AppController.collectionController.findRequestById(idCollection, id);
                AppController.openTabContentHttp(request);
            } else if (relativeX >= 206 && relativeX <= 220) {
                final Request request = AppController.collectionController.findRequestById(idCollection, id);
                final String name = InputDialog.showDialog(LangManager.text("app.inputDialog.editRequest.title.text"),
                        request.getName());
                if (name != null) {
                    request.setName(name);
                    AppController.collectionController.updateRequestById(idCollection, id, request);
                    AppController.renderNodesTreeCollections();
                }
            } else if (relativeX >= 221 && relativeX <= 240) {
                Option option = ConfirmDialog.show(LangManager.text("confirmDialog.deleteRequest.message.text"));
                if (option == Option.YES) {
                    AppController.collectionController.deleteRequestById(idCollection, id);
                    AppController.renderNodesTreeCollections();
                    MessageDialog.showMessage(
                            Type.SUCCESS,
                            LangManager.text("app.messageDialog.deleted.text"));
                }

            }
        } else {
            System.out.println("big");
        }
    }

}