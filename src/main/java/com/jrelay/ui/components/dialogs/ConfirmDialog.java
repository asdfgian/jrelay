package com.jrelay.ui.components.dialogs;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import com.jrelay.core.utils.i18n.LangManager;
import com.jrelay.ui.controllers.AppController;
import com.jrelay.ui.shared.styles.Colors;
import com.jrelay.ui.shared.styles.Style;
import com.jrelay.ui.shared.utils.template.Struct;

import java.awt.Cursor;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicReference;

import net.miginfocom.swing.MigLayout;

public class ConfirmDialog extends JPanel implements Struct {

    private final JLabel titleLabel = new JLabel(LangManager.text("confirmDialog.titleLabel.text"));
    private final JLabel message = new JLabel();
    private final JButton yesButton = new JButton(LangManager.text("confirmDialog.yesButton.text"));
    private final JButton noButton = new JButton(LangManager.text("confirmDialog.noButton.text"));
    private final JPanel buttons = new JPanel(new MigLayout("insets 0, gap 10", "push[]10[]push"));

    public enum Option {
        YES, NO
    }

    private ConfirmDialog() {
        this.build();
    }

    @Override
    public void initComponents() {
        //
    }

    @Override
    public void configureStyle() {
        Style.setLayout(this, new MigLayout("wrap 1, insets 20", "[grow, fill]"));
        Style.setFontSize(titleLabel, 18f);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        Style.setCursor(yesButton, Cursor.HAND_CURSOR);
        Style.setCursor(noButton, Cursor.HAND_CURSOR);
        Style.setBackgroundColor(noButton, Colors.SECONDARY_COLOR);
        Style.setUndecoratedButton(noButton);
    }

    @Override
    public void compose() {
        this.add(titleLabel, "align center, gapbottom 10");
        this.add(message, "growx, gapbottom 15");
        buttons.add(yesButton, "h 30!, w 60!");
        buttons.add(noButton, "h 30!, w 60!");
        this.add(buttons, "growx");
    }

    public static Option show(String msg) {
        ConfirmDialog panel = new ConfirmDialog();
        panel.message.setText(msg);

        final JDialog dialog = new JDialog((Frame) null, true);
        final AtomicReference<Option> result = new AtomicReference<>(Option.NO);

        dialog.setUndecorated(true);
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        dialog.getContentPane().add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);

        boolean wasGlassPaneActive = AppController.isGlassPaneActive();

        ActionListener closeDialog = e -> {
            dialog.dispose();
            if (!wasGlassPaneActive) {
                AppController.hideGlassPane();
            }
        };

        panel.yesButton.addActionListener(e -> {
            result.set(Option.YES);
            closeDialog.actionPerformed(e);
        });

        panel.noButton.addActionListener(e -> {
            result.set(Option.NO);
            closeDialog.actionPerformed(e);
        });

        if (!wasGlassPaneActive) {
            AppController.showGlassPane();
        }

        dialog.setVisible(true);
        return result.get();
    }

}
