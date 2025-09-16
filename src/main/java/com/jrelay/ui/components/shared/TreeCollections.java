package com.jrelay.ui.components.shared;

import java.awt.Component;
import java.awt.Cursor;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.jrelay.core.models.Collection;
import com.jrelay.core.models.request.Request;
import com.jrelay.core.utils.i18n.LangManager;
import com.jrelay.ui.components.shared.controllers.TreeCollectionsListener;
import com.jrelay.ui.components.shared.models.CollectionData;
import com.jrelay.ui.components.shared.models.RequestData;
import com.jrelay.ui.controllers.AppController;
import com.jrelay.ui.shared.styles.Colors;
import com.jrelay.ui.shared.styles.Style;
import com.jrelay.ui.shared.utils.UiUtils;
import com.jrelay.ui.shared.utils.template.Struct;
import com.jrelay.ui.shared.utils.template.Translatable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.miginfocom.swing.MigLayout;

public class TreeCollections extends JPanel implements Struct, Translatable {

    private final JPanel headerContainer = new JPanel(new MigLayout("aligny 50%"));
    @Getter
    private final JButton newButton = new JButton(LangManager.text("app.panel.newButton.text"), UiUtils.ADD_ICON);
    @Getter
    private final JTree tree = new JTree();
    private JScrollPane scroll = new JScrollPane();
    @Getter
    private int widthCell = 170;

    public TreeCollections() {
        this.build();
        this.renderNodes();
        LangManager.register(this);
    }

    public TreeCollections(int widthCell) {
        this.widthCell = widthCell;
        this.build();
        this.renderNodes();
        LangManager.register(this);
    }

    @Override
    public void initComponents() {
    }

    @Override
    public void configureStyle() {
        Style.setLayout(this, new MigLayout("fill, insets 5 0 5 0", "[grow]", "[]1[grow]"));
        tree.setCellRenderer(new NodeRenderer());
        tree.setRootVisible(false);
        Style.setFontSize(newButton, 13f);
        Style.setUndecoratedButton(newButton);
        Style.setBackgroundColor(newButton, Colors.SECONDARY_COLOR);
        Style.setTextColor(newButton, Colors.ICON_COLOR);
        Style.setCursor(newButton, Cursor.HAND_CURSOR);
    }

    @Override
    public void attachLogic() {
        TreeCollectionsListener.setupCellButtonsListener(this);
    }

    @Override
    public void compose() {
        headerContainer.add(newButton, "h 30!");
        this.add(headerContainer, "grow, h 40!, wrap");
        scroll.setViewportView(tree);
        this.add(scroll, "grow");
    }

    @Override
    public void updateText() {
        Style.setButtonText(newButton, LangManager.text("app.panel.newButton.text"));
    }

    public void renderNodes() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("collections");
        DefaultTreeModel model = new DefaultTreeModel(root);
        tree.setModel(model);

        for (var collection : AppController.collectionController.findAll()) {
            final var collectionData = new CollectionData(collection.getId(), collection.getName());
            DefaultMutableTreeNode collectionNode = new DefaultMutableTreeNode(collectionData);

            if (collection.getRequests() != null) {
                for (var request : collection.getRequests()) {
                    collectionNode.add(new DefaultMutableTreeNode(
                            new RequestData(request.getIdRequest(), collection.getId(), request.getMethod().toString(),
                                    request.getName())));
                }
            }
            root.add(collectionNode);
        }

        expandAll(tree, new TreePath(root));
    }

    private void expandAll(JTree tree, TreePath parent) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();

        if (node.getChildCount() >= 0) {
            for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
                TreeNode child = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(child);
                expandAll(tree, path);
            }
        }

        tree.expandPath(parent);
    }

    public void renderFilteredNodes(List<Object> results) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("collections");
        DefaultTreeModel model = new DefaultTreeModel(root);
        tree.setModel(model);

        Map<String, DefaultMutableTreeNode> collectionNodes = new HashMap<>();

        for (Object result : results) {
            if (result instanceof Collection collection) {
                var collectionNode = new DefaultMutableTreeNode(
                        new CollectionData(collection.getId(), collection.getName()));
                root.add(collectionNode);
                collectionNodes.put(collection.getId(), collectionNode);
            }

            if (result instanceof Request request) {
                var collection = AppController.collectionController.findAll().stream()
                        .filter(c -> c.getRequests().contains(request))
                        .findFirst()
                        .orElse(null);

                if (collection != null) {
                    DefaultMutableTreeNode collectionNode = collectionNodes.computeIfAbsent(
                            collection.getId(),
                            id -> {
                                var node = new DefaultMutableTreeNode(
                                        new CollectionData(collection.getId(), collection.getName()));
                                root.add(node);
                                return node;
                            });

                    collectionNode.add(new DefaultMutableTreeNode(
                            new RequestData(
                                    request.getIdRequest(),
                                    collection.getId(),
                                    request.getMethod().toString(),
                                    request.getName())));
                }
            }
        }

        if (root.getChildCount() > 0) {
            TreePath rootPath = new TreePath(root.getPath());
            tree.expandPath(rootPath);
            tree.scrollPathToVisible(rootPath.pathByAddingChild(root.getFirstChild()));
        }
    }

    @NoArgsConstructor
    private class NodeRenderer implements TreeCellRenderer {

        private final CollectionCell collectionCell = new CollectionCell();
        private final RequestCell requestCell = new RequestCell();

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

            Component render = collectionCell;

            if (value instanceof DefaultMutableTreeNode node) {
                Object userObject = node.getUserObject();

                if (userObject instanceof CollectionData collectionData) {
                    collectionCell.getNameLabel().setText(collectionData.name());
                    render = collectionCell;
                }
                if (userObject instanceof RequestData requestData) {
                    requestCell.setMethodLabel(requestData.method());
                    requestCell.getRequestNameLabel().setText(requestData.name());
                    render = requestCell;
                }
            }
            return render;
        }

        private class CollectionCell extends JPanel implements Struct {
            private final JLabel iconLabel = new JLabel(UiUtils.TREE_FOLDER_ICON);
            @Getter
            private JLabel nameLabel = new JLabel();
            private final JLabel addFileChild = new JLabel(UiUtils.FILE_ADD_ICON);
            private final JLabel addFolderChild = new JLabel(UiUtils.ADD_FOLDER_ICON);
            private final JLabel removeFolder = new JLabel(UiUtils.TRASH_TREE_ICON);

            CollectionCell() {
                this.build();
            }

            @Override
            public void initComponents() {
            }

            @Override
            public void configureStyle() {
                if (widthCell == 170) {
                    Style.setLayout(this, new MigLayout("fillx, aligny 50%, insets 5", "[]5[]push[]5[]5[]"));
                } else {
                    Style.setLayout(this, new MigLayout("fillx, aligny 50%, insets 5", "[]5[]push[]5[]"));
                }
                Style.setTransparent(this);
                Style.setFontSize(nameLabel, 12);
            }

            @Override
            public void compose() {
                this.add(iconLabel, "grow, w 20!");
                this.add(nameLabel, "growx, w " + widthCell + "!");
                if (widthCell == 170) {
                    this.add(addFileChild, "grow");
                }
                this.add(addFolderChild, "grow");
                this.add(removeFolder, "grow");
            }

        }

        private class RequestCell extends JPanel implements Struct {
            private final JLabel methodName = new JLabel("GET");
            @Getter
            private final JLabel requestNameLabel = new JLabel();
            private final JLabel editButton = new JLabel(UiUtils.EDIT_ICON);
            private final JLabel deleteButton = new JLabel(UiUtils.TRASH_TREE_ICON);

            RequestCell() {
                this.build();
            }

            @Override
            public void initComponents() {
            }

            @Override
            public void configureStyle() {
                Style.setTransparent(this);
                Style.setLayout(this, new MigLayout("fillx, insets 5, aligny 50%", "[]5[]push[][]"));
                requestNameLabel.setHorizontalAlignment(SwingConstants.LEFT);
                Style.setTextColor(methodName, Style.getColorByMethod("GET"));
                Style.setFontSize(methodName, 12);
                Style.setFontSize(requestNameLabel, 12);
            }

            @Override
            public void compose() {
                this.add(methodName, "grow, w 55!");
                if (widthCell == 170) {
                    this.add(requestNameLabel, "growx, w " + (widthCell - 30) + "!");
                } else {
                    this.add(requestNameLabel, "growx, w " + (widthCell - 51) + "!");
                }
                this.add(editButton, "grow");
                this.add(deleteButton, "grow");
            }

            public void setMethodLabel(String method) {
                Style.setTextColor(methodName, Style.getColorByMethod(method));
                this.methodName.setText(method);
            }

        }
    }

}
