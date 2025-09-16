package com.jrelay.ui.components.shared.statics;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.jrelay.core.utils.i18n.LangManager;
import com.jrelay.ui.shared.styles.Style;
import com.jrelay.ui.shared.utils.UiUtils;
import com.jrelay.ui.shared.utils.template.Struct;
import com.jrelay.ui.shared.utils.template.Translatable;

import net.miginfocom.swing.MigLayout;

public class HttpWelcomePanel extends JPanel implements Struct, Translatable {
    private final JLabel icon = new JLabel(UiUtils.EXCHANGE_ICON);
    private final JLabel titleLabel = new JLabel();
    private final JLabel subtitleLabel = new JLabel();

    public HttpWelcomePanel() {
        this.build();
        this.updateText();
        LangManager.register(this);
    }

    @Override
    public void initComponents() {
    }

    @Override
    public void configureStyle() {
        Style.setLayout(this, new MigLayout("fillx, insets 30, aligny 50%", "[center]", "[]10[][]"));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        Style.setFontSize(titleLabel, 20);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    @Override
    public void compose() {
        this.add(icon, "center, wrap");
        this.add(titleLabel, "wrap, center");
        this.add(subtitleLabel, "center");
    }

    @Override
    public void updateText() {
        Style.setLabelText(titleLabel, LangManager.text("httpWelcomePanel.titleLabel.text"));
        Style.setLabelText(subtitleLabel, LangManager.text("httpWelcomePanel.subtitleLabel.text"));
    }
}
