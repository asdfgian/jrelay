package com.jrelay.ui.components.shared.statics;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import com.jrelay.core.utils.i18n.LangManager;
import com.jrelay.ui.shared.styles.Style;
import com.jrelay.ui.shared.utils.template.Struct;
import com.jrelay.ui.shared.utils.template.Translatable;

import net.miginfocom.swing.MigLayout;

public class WaitingPanel extends JPanel implements Struct, Translatable {

    private final JLabel loadingLabel = new JLabel();
    private final JProgressBar progress = new JProgressBar();

    public WaitingPanel() {
        this.build();
        this.updateText();
        LangManager.register(this);
    }

    @Override
    public void initComponents() {
    }

    @Override
    public void configureStyle() {
        Style.setLayout(this, new MigLayout("fillx, insets 100 150 100 150, aligny 50%", "[center]", "[]10[]"));

        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);

        progress.setIndeterminate(true);
        progress.setPreferredSize(new Dimension(200, 20));
    }

    @Override
    public void compose() {
        this.add(loadingLabel, "wrap");
        this.add(progress, "center");
    }

    @Override
    public void updateText() {
        Style.setLabelText(loadingLabel, LangManager.text("waitingPanel.loadingLabel.text"));
    }
}
