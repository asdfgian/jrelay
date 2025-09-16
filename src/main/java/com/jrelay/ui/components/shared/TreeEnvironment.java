package com.jrelay.ui.components.shared;

import java.awt.Component;
import java.awt.Cursor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.jrelay.core.models.Environment;
import com.jrelay.core.utils.i18n.LangManager;
import com.jrelay.ui.components.shared.controllers.TreePanelEnvironmentListener;
import com.jrelay.ui.components.shared.models.EnvironmentData;
import com.jrelay.ui.controllers.AppController;
import com.jrelay.ui.shared.styles.Colors;
import com.jrelay.ui.shared.styles.Style;
import com.jrelay.ui.shared.utils.UiUtils;
import com.jrelay.ui.shared.utils.template.Struct;
import com.jrelay.ui.shared.utils.template.Translatable;

import lombok.Getter;
import net.miginfocom.swing.MigLayout;

public class TreeEnvironment extends JPanel implements Struct, Translatable {

    private final JPanel headerContainer = new JPanel(new MigLayout("aligny 50%"));
    @Getter
    private final JButton newEnvButton = new JButton();
    @Getter
    private final JTree tree = new JTree();
    private JScrollPane scroll = new JScrollPane();

    public TreeEnvironment() {
        this.build();
        this.updateText();
        this.renderNodes();
        LangManager.register(this);
    }

    @Override
    public void initComponents() {
    }

    @Override
    public void configureStyle() {
        Style.setLayout(this, new MigLayout("fill, insets 5 0 5 0", "[grow]", "[]1[grow]"));
        tree.setRootVisible(false);
        tree.setCellRenderer(new NodeRenderer());
        Style.setUndecoratedButton(newEnvButton);
        Style.setFontSize(newEnvButton, 13f);
        Style.setBackgroundColor(newEnvButton, Colors.SECONDARY_COLOR);
        Style.setIcon(newEnvButton, UiUtils.ADD_ICON);
        Style.setTextColor(newEnvButton, Colors.ICON_COLOR);
        Style.setCursor(newEnvButton, Cursor.HAND_CURSOR);
    }

    @Override
    public void attachLogic() {
        TreePanelEnvironmentListener.setupCellButtonsListener(tree);
    }

    @Override
    public void compose() {
        headerContainer.add(newEnvButton, "h 30!");
        this.add(headerContainer, "grow, h 40!, wrap");
        scroll.setViewportView(tree);
        this.add(scroll, "grow");
    }

    @Override
    public void updateText() {
        Style.setButtonText(newEnvButton, LangManager.text("app.panel.newButton.text"));
    }

    public void renderNodes() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("environments");
        DefaultTreeModel model = new DefaultTreeModel(root);
        tree.setModel(model);

        for (var env : AppController.environmentController.findAll()) {
            final var envData = new EnvironmentData(env.getId(), env.getName());
            final var envNode = new DefaultMutableTreeNode(envData);
            root.add(envNode);
        }

        if (root.getChildCount() > 0) {
            TreePath rootPath = new TreePath(root.getPath());
            tree.expandPath(rootPath);
            tree.scrollPathToVisible(rootPath.pathByAddingChild(root.getFirstChild()));
        }
    }

    public void renderFilteredNodes(List<Object> results) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("environments");
        DefaultTreeModel model = new DefaultTreeModel(root);
        tree.setModel(model);

        Map<String, DefaultMutableTreeNode> collectionNodes = new HashMap<>();

        for (Object result : results) {
            if (result instanceof Environment environment) {
                var environmentNode = new DefaultMutableTreeNode(
                        new EnvironmentData(environment.getId(), environment.getName()));
                root.add(environmentNode);
                collectionNodes.put(environment.getId(), environmentNode);
            }
        }

        if (root.getChildCount() > 0) {
            TreePath rootPath = new TreePath(root.getPath());
            tree.expandPath(rootPath);
            tree.scrollPathToVisible(rootPath.pathByAddingChild(root.getFirstChild()));
        }
    }

    private class NodeRenderer implements TreeCellRenderer {

        private final EnvironmentCell environmentCell = new EnvironmentCell();

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                boolean leaf, int row, boolean hasFocus) {
            Component render = environmentCell;

            if (value instanceof DefaultMutableTreeNode node) {
                Object userObject = node.getUserObject();

                if (userObject instanceof EnvironmentData environmentData) {
                    environmentCell.getNameLabel().setText(environmentData.name());
                    render = environmentCell;
                }
            }

            return render;
        }

    }

    private class EnvironmentCell extends JPanel implements Struct {

        private final JLabel iconLabel = new JLabel(new FlatSVGIcon("icon/stack.svg", 0.55f)
                .setColorFilter(new FlatSVGIcon.ColorFilter(color -> Colors.ICON_COLOR)));
        private final JLabel edit = new JLabel(UiUtils.EDIT_ICON);
        private final JLabel share = new JLabel(UiUtils.SHARE_ICON);
        private final JLabel remove = new JLabel(UiUtils.TRASH_TREE_ICON);
        @Getter
        private JLabel nameLabel = new JLabel();

        EnvironmentCell() {
            this.build();
        }

        @Override
        public void initComponents() {
        }

        @Override
        public void configureStyle() {
            Style.setLayout(this, new MigLayout("fillx, aligny 50%, insets 5", "[]5[grow]push[][][]"));
            Style.setTransparent(this);
            Style.setFontSize(nameLabel, 12);
        }

        @Override
        public void compose() {
            this.add(iconLabel, "grow, w 20!");
            this.add(nameLabel, "growx, w 170!");
            this.add(edit, "grow");
            this.add(share, "grow");
            this.add(remove, "grow");
        }

    }
}
