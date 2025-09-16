package com.jrelay.ui.components.shared.models;

import javax.swing.JComponent;

import com.jrelay.core.utils.i18n.LangManager;
import com.jrelay.ui.components.dialogs.ConfirmDialog;
import com.jrelay.ui.components.dialogs.InputDialog;
import com.jrelay.ui.components.dialogs.MessageDialog;
import com.jrelay.ui.components.dialogs.ConfirmDialog.Option;
import com.jrelay.ui.components.dialogs.MessageDialog.Type;
import com.jrelay.ui.components.shared.TreeCollections;
import com.jrelay.ui.controllers.AppController;

public record CollectionData(String id, String name) implements ClickableNode {
    @Override
    public void handleClick(int relativeX, JComponent jComponent) {
        final var treeCollections = ((TreeCollections) jComponent);
        final int WIDTH = treeCollections.getWidthCell();
        if (WIDTH < 300) {
            if (relativeX >= 203 && relativeX <= 213) {
                String requestName = InputDialog.showDialog(LangManager.text("app.inputDialog.newRequest.title.text"));
                if (requestName == null)
                    return;
                AppController.addTabContentHttp(id, requestName);
                treeCollections.renderNodes();
            } else if (relativeX >= 223 && relativeX <= 233) {
                String name = InputDialog.showDialog(LangManager.text("app.inputDialog.newCollection.title.text"));
                if (name == null)
                    return;
            } else if (relativeX >= 243 && relativeX <= 253) {
                Option option = ConfirmDialog.show(LangManager.text("confirmDialog.deleteCollection.message.text"));
                if (option == Option.YES) {
                    AppController.collectionController.deleteCollectionById(id);
                    treeCollections.renderNodes();
                    MessageDialog.showMessage(Type.SUCCESS, LangManager.text("app.messageDialog.deleted.text"));
                }
            }
        } else {
            if (relativeX >= 355 && relativeX <= 365) { // add collection
                String name = InputDialog.showDialog(LangManager.text("app.inputDialog.newCollection.title.text"));
                if (name == null)
                    return;
            } else if (relativeX >= 375 && relativeX <= 385) {
                Option option = ConfirmDialog.show(LangManager.text("confirmDialog.deleteCollection.message.text"));
                if (option == Option.YES) {
                    AppController.collectionController.deleteCollectionById(id);
                    treeCollections.renderNodes();
                    AppController.renderNodesTreeCollections();
                    MessageDialog.showMessage(Type.SUCCESS, LangManager.text("app.messageDialog.deleted.text"));
                }
            }
        }
    }
}