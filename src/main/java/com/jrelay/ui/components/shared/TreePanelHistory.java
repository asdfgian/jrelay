package com.jrelay.ui.components.shared;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import com.jrelay.ui.components.shared.controllers.TreePanelHistoryListener;
import com.jrelay.ui.components.shared.models.RequestHistoryData;
import com.jrelay.ui.controllers.AppController;
import com.jrelay.ui.shared.styles.Style;
import com.jrelay.ui.shared.utils.UiUtils;
import com.jrelay.ui.shared.utils.template.Struct;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.miginfocom.swing.MigLayout;

public class TreePanelHistory extends JPanel implements Struct {

    @Getter
    private final JTree tree = new JTree();
    private JScrollPane scroll = new JScrollPane();
    private int widthCell = 164;

    public TreePanelHistory() {
        this.build();
        this.renderNodes();
    }

    @Override
    public void initComponents() {
        ToolTipManager.sharedInstance().registerComponent(tree);
    }

    @Override
    public void configureStyle() {
        Style.setLayout(this, new MigLayout("fill, insets 0"));
        tree.setCellRenderer(new NodeRenderer());
        tree.setRootVisible(false);
    }

    @Override
    public void attachLogic() {
        TreePanelHistoryListener.setupCellButtonsListener(tree);
    }

    @Override
    public void compose() {
        scroll.setViewportView(tree);
        this.add(scroll, "grow");
    }

    public void renderNodes() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("history");
        DefaultTreeModel model = new DefaultTreeModel(root);
        tree.setModel(model);

        for (var reqHistory : AppController.requestHistoryController.findAll()) {
            final var req = reqHistory.request();
            final var requestHistoryData = new RequestHistoryData(
                    req.getIdRequest(),
                    req.getIdCollection(),
                    req.getMethod().name(),
                    req.getName(),
                    reqHistory.timestamp());
            final var historyNode = new DefaultMutableTreeNode(requestHistoryData);
            root.add(historyNode);
        }

        if (root.getChildCount() > 0) {
            TreePath rootPath = new TreePath(root.getPath());
            tree.expandPath(rootPath);
            tree.scrollPathToVisible(rootPath.pathByAddingChild(root.getFirstChild()));
        }
    }

    @NoArgsConstructor
    private class NodeRenderer implements TreeCellRenderer {

        private final RequestCell requestCell = new RequestCell();

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

            Component render = requestCell;

            if (value instanceof DefaultMutableTreeNode node) {
                Object userObject = node.getUserObject();
                if (userObject instanceof RequestHistoryData data) {
                    requestCell.setMethodLabel(data.method());
                    requestCell.getRequestName().setText(data.name());
                    requestCell.setToolTipText(data.timestamp());
                    render = requestCell;
                }
            }
            return render;
        }

        private class RequestCell extends JPanel implements Struct {
            private final JLabel methodName = new JLabel();
            @Getter
            private final JLabel requestName = new JLabel();
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
                Style.setLayout(this, new MigLayout("fillx, insets 5, aligny 50%", "[]5[]push[]"));
                requestName.setHorizontalAlignment(SwingConstants.LEFT);
                Style.setTextColor(methodName, Style.getColorByMethod("GET"));
                Style.setFontSize(methodName, 12);
                Style.setFontSize(requestName, 12);
            }

            @Override
            public void compose() {
                this.add(methodName, "grow, w 55!");
                this.add(requestName, "growx, w " + widthCell + "!");
                this.add(deleteButton, "grow");
            }

            public void setMethodLabel(String method) {
                Style.setTextColor(methodName, Style.getColorByMethod(method));
                this.methodName.setText(method);
            }

        }
    }

}
