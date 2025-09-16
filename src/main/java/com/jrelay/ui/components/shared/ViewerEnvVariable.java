package com.jrelay.ui.components.shared;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;

import com.jrelay.core.models.Environment;
import com.jrelay.core.models.Environment.Variable;
import com.jrelay.ui.components.dialogs.SaveEnvDialog;
import com.jrelay.ui.shared.styles.Colors;
import com.jrelay.ui.shared.styles.Style;
import com.jrelay.ui.shared.utils.UiUtils;
import com.jrelay.ui.shared.utils.template.Struct;

import lombok.Getter;
import net.miginfocom.swing.MigLayout;

public class ViewerEnvVariable extends JPanel implements Struct {

    private static final int WIDTH = 500;
    private final int HEIGHT = 300;
    private final JPanel containerTitleEnv = new JPanel(new MigLayout("fillx", "[]push[]"));
    private final JLabel titleEnvLabel = new JLabel("");
    @Getter
    private final JButton editCurrentEnv = new JButton("Edit", UiUtils.EDIT_ICON);

    private final JPanel containerTitleGlobal = new JPanel(new MigLayout("fillx", "[]push[]"));
    private final JLabel titleGlobalLabel = new JLabel("Global");
    private final JButton editGlobal = new JButton("Add", UiUtils.ADD_ICON);

    private final String[] COLUMN_NAMES = { "VARIABLE", "INITIAL VALUE", "CURRENT VALUE" };

    JTable table = new JTable();
    JScrollPane scrollPane = new JScrollPane(table);

    public ViewerEnvVariable() {
        this.build();
    }

    @Override
    public void initComponents() {
    }

    @Override
    public void configureStyle() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        Style.setLayout(this, new MigLayout("fill, insets 5", "[grow]", "[][grow]0[]"));
        Style.setBackgroundColor(containerTitleEnv, Colors.SECONDARY_COLOR);
        Style.setBackgroundColor(containerTitleGlobal, Colors.SECONDARY_COLOR);
        Style.setBold(titleEnvLabel);
        Style.setBold(titleGlobalLabel);
        Style.setBackgroundColor(this, Colors.SECONDARY_COLOR);

        Style.setFontSize(editCurrentEnv, 12.1f);
        Style.setCursor(editCurrentEnv, Cursor.HAND_CURSOR);
        Style.setUndecoratedButton(editCurrentEnv);
        Style.setBackgroundColor(editCurrentEnv, Colors.SECONDARY_COLOR);

        Style.setFontSize(editGlobal, 12.1f);
        Style.setCursor(editGlobal, Cursor.HAND_CURSOR);
        Style.setUndecoratedButton(editGlobal);
        Style.setBackgroundColor(editGlobal, Colors.SECONDARY_COLOR);

        table.setBorder(null);
        Style.setFontSize(table, 12.1f);
    }

    @Override
    public void compose() {
        containerTitleEnv.add(titleEnvLabel, "growx");
        containerTitleEnv.add(editCurrentEnv, "grow");
        this.add(containerTitleEnv, "growx, wrap");
        this.add(scrollPane, "grow, wrap");
        containerTitleGlobal.add(titleGlobalLabel, "growx");
        containerTitleGlobal.add(editGlobal, "grow");
        this.add(containerTitleGlobal, "growx");
    }

    public void setTitle(String title) {
        titleEnvLabel.setText(title.toUpperCase());
    }

    public void loadVariablesIntoTable(List<Variable> variables) {
        DefaultTableModel model = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Variable var : variables) {
            model.addRow(new Object[] {
                    var.getKey(),
                    var.getInitialValue(),
                    var.getCurrentValue()
            });
        }
        table.setModel(model);
    }

    public static void showView(Component parent, Environment environment) {
        JPopupMenu popup = new JPopupMenu();
        final var viewerEnvVariable = new ViewerEnvVariable();
        viewerEnvVariable.setTitle(environment.getName());
        viewerEnvVariable.loadVariablesIntoTable(environment.getVariables());
        viewerEnvVariable.getEditCurrentEnv().addActionListener(e -> {
            SaveEnvDialog.showEditEnvDialog(environment);
        });
        popup.setFocusable(false);
        popup.add(viewerEnvVariable);
        popup.registerKeyboardAction(arg0 -> popup.setVisible(false),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        popup.show(parent,
                -1 * (WIDTH - parent.getWidth() + 6),
                parent.getHeight() + 10);
    }

}
