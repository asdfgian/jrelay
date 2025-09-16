package com.jrelay.ui.views;

import java.awt.Cursor;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import com.jrelay.core.utils.i18n.LangManager;
import com.jrelay.ui.shared.styles.Colors;
import com.jrelay.ui.shared.styles.Style;
import com.jrelay.ui.shared.utils.UiUtils;
import com.jrelay.ui.shared.utils.template.Struct;
import com.jrelay.ui.shared.utils.template.Translatable;

import lombok.Getter;
import net.miginfocom.swing.MigLayout;

@Getter
public class Footer extends JPanel implements Struct, Translatable {

    private final JToggleButton toggleSideBarButton = new JToggleButton(UiUtils.SIDEBAR_RIGHT_ICON);
    private final JButton consoleButton = new JButton("Console", UiUtils.TERMINAL_ICON);
    private final JButton helpButton = new JButton();
    private final JButton contributeButton = new JButton();
    private JToggleButton orientationButton = new JToggleButton(UiUtils.SIDEBAR_TOP_ICON);

    public Footer() {
        this.build();
        this.updateText();
        LangManager.register(this);
    }

    @Override
    public void initComponents() {
    }

    @Override
    public void configureStyle() {
        Style.setLayout(this, new MigLayout("fill, aligny 50%", "15[]20[]push[]3[]20[]15"));
        Style.setTransparent(toggleSideBarButton);
        Style.setCursor(toggleSideBarButton, Cursor.HAND_CURSOR);
        Style.setTransparent(consoleButton);
        Style.setCursor(consoleButton, Cursor.HAND_CURSOR);
        Style.setFontSize(consoleButton, 12f);
        Style.setTextColor(consoleButton, Colors.ICON_COLOR);
        Style.setFontSize(helpButton, 12f);
        Style.setTextColor(helpButton, Colors.ICON_COLOR);
        Style.setTransparent(helpButton);
        Style.setIcon(helpButton, UiUtils.HELP_ICON);
        Style.setCursor(helpButton, Cursor.HAND_CURSOR);
        Style.setTransparent(orientationButton);
        Style.setCursor(orientationButton, Cursor.HAND_CURSOR);
        Style.setFontSize(contributeButton, 12f);
        Style.setTextColor(contributeButton, Colors.ICON_COLOR);
        Style.setTransparent(contributeButton);
        Style.setCursor(contributeButton, Cursor.HAND_CURSOR);
        Style.setIcon(contributeButton, UiUtils.GITHUB_ICON);
    }

    @Override
    public void compose() {
        this.add(toggleSideBarButton, "w 20!, h 20!");
        this.add(consoleButton, "w 100!, h 20!");
        this.add(helpButton, "h 20!");
        this.add(contributeButton, "h 20!");
        this.add(orientationButton, "w 20!, h 20!");
    }

    @Override
    public void updateText() {
        Style.setToolTip(toggleSideBarButton, LangManager.text("footer.toggleSideBarButton.expand.placeholder.text"));
        Style.setButtonText(helpButton, LangManager.text("footer.helpButton.text"));
        Style.setButtonText(contributeButton, LangManager.text("footer.contributeButton.text"));
    }

}
