package com.jrelay.ui.components.dialogs.listener;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.jrelay.core.models.Collection;
import com.jrelay.core.models.request.Request;
import com.jrelay.core.utils.i18n.LangManager;
import com.jrelay.ui.components.dialogs.InputDialog;
import com.jrelay.ui.components.dialogs.MessageDialog;
import com.jrelay.ui.components.dialogs.SaveRequestDialog;
import com.jrelay.ui.components.dialogs.MessageDialog.Location;
import com.jrelay.ui.components.dialogs.MessageDialog.Type;
import com.jrelay.ui.components.shared.ClosableTab;
import com.jrelay.ui.components.shared.models.CollectionData;
import com.jrelay.ui.controllers.AppController;

public class SaveRequestDialogListener {

    public static void setupNewCollectionButtonListener(SaveRequestDialog saveRequestDialog) {
        final var button = saveRequestDialog.getTreeCollections().getNewButton();
        final var tree = saveRequestDialog.getTreeCollections();
        button.addActionListener(e -> {
            String name = InputDialog.showDialog(LangManager.text("app.inputDialog.newCollection.title.text"));
            if (name == null)
                return;
            AppController.collectionController.saveCollection(new Collection(name, new ArrayList<>()));
            tree.renderNodes();
            AppController.renderNodesTreeCollections();
        });
    }

    /**
     *
     * Registers an ActionListener to the Save button in the SaveDialog.
     * Validates the request name and collection selection before saving or updating
     * the request.
     * 
     * @author @ASDG14N
     * @since 07-08-2025
     */
    public static void setupSaveButtonListener(
            JDialog dialog,
            SaveRequestDialog saveDialog,
            Request request,
            ClosableTab closableTab) {
        final var saveButton = saveDialog.getSaveButton();

        saveButton.addActionListener(e -> {
            final String requestName = saveDialog.getRequestName();

            if (isRequestNameInvalid(requestName)) {
                MessageDialog.showMessage(
                        Location.TOP_CENTER,
                        Type.ERROR,
                        LangManager.text("saveRequestDialogListener.messageDialog.message1.text"));
                return;
            }

            request.setName(requestName);
            TreePath selectedPath = saveDialog.getTreeCollections().getTree().getSelectionPath();

            if (selectedPath != null && isCollectionSelected(selectedPath)) {
                saveNewRequest(dialog, request, selectedPath);
                closableTab.setTitle(requestName);
                closableTab.restore();
            } else {
                MessageDialog.showMessage(
                        Location.TOP_CENTER,
                        Type.ERROR,
                        LangManager.text("saveRequestDialogListener.messageDialog.message2.text"));
            }
        });
    }

    /**
     * Checks if the request name is null or blank.
     */
    private static boolean isRequestNameInvalid(String requestName) {
        return requestName == null || requestName.isBlank();
    }

    /**
     * Checks whether the selected TreePath contains a valid collection node.
     */
    private static boolean isCollectionSelected(TreePath path) {
        Object node = path.getLastPathComponent();
        return node instanceof DefaultMutableTreeNode treeNode &&
                treeNode.getUserObject() instanceof CollectionData;
    }

    /**
     * Saves a new request into the selected collection.
     */
    private static void saveNewRequest(JDialog dialog, Request request, TreePath path) {
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) path.getLastPathComponent();
        CollectionData collectionData = (CollectionData) treeNode.getUserObject();

        request.setIdCollection(collectionData.id());

        AppController.collectionController.saveRequest(request);

        closeDialogWithSuccess(dialog);
    }

    /**
     * Finalizes the save operation, hides the glass pane, and notifies success.
     */
    private static void closeDialogWithSuccess(JDialog dialog) {
        dialog.dispose();
        AppController.hideGlassPane();
        AppController.renderNodesTreeCollections();
        MessageDialog.showMessage(
                Type.SUCCESS,
                LangManager.text("app.messageDialog.saved.text"));
    }

    public static void setupSearchTextFieldListener(SaveRequestDialog saveDialog) {
        final var search = saveDialog.getSearchTextField();
        final var tree = saveDialog.getTreeCollections();
        search.onChange(text -> {
            String query = text.trim().toLowerCase();
            if (query.isEmpty()) {
                tree.renderNodes();
                return;
            }

            List<Object> results = new ArrayList<>();

            for (var collection : AppController.collectionController.findAll()) {
                if (collection.getName() != null && collection.getName().toLowerCase().contains(query)) {
                    results.add(collection);
                }
            }

            tree.renderFilteredNodes(results);
        });
    }
}
