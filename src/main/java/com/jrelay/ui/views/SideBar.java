package com.jrelay.ui.views;

import java.awt.Cursor;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;

import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import com.formdev.flatlaf.FlatClientProperties;
import com.jrelay.core.models.Preference.Lang;
import com.jrelay.core.utils.i18n.LangManager;
import com.jrelay.ui.components.shared.SyntaxEditor;
import com.jrelay.ui.components.shared.TabIcon;
import com.jrelay.ui.components.shared.TextField;
import com.jrelay.ui.components.shared.TreeCollections;
import com.jrelay.ui.components.shared.TreeEnvironment;
import com.jrelay.ui.components.shared.TreePanelHistory;
import com.jrelay.ui.shared.styles.Colors;
import com.jrelay.ui.shared.styles.Style;
import com.jrelay.ui.shared.utils.UiUtils;
import com.jrelay.ui.shared.utils.animate.Animator;
import com.jrelay.ui.shared.utils.template.Struct;
import com.jrelay.ui.shared.utils.template.Translatable;

import lombok.Getter;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;

@Getter
public class SideBar extends JPanel implements Struct {

    @Setter
    private Animator animator;
    private final int closeWidth = 55;
    private final int openWidth = 350;
    @Setter
    private boolean open = false;

    @Getter
    private JTabbedPane tabbedPane;
    @Getter
    private final HttpPanel httpPanel = new HttpPanel();
    private SettingsPanel settingsPanel;

    public SideBar() {
        this.build();
    }

    @Override
    public void initComponents() {
        tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
        settingsPanel = new SettingsPanel();
    }

    @Override
    public void configureStyle() {
        Style.setLayout(this, new MigLayout("fill, insets 0"));
        Style.setTabTypeCard(tabbedPane);
    }

    @Override
    public void compose() {
        tabbedPane.addTab("", httpPanel);
        tabbedPane.setTabComponentAt(0, new TabIcon(UiUtils.HTTP_ICON));
        tabbedPane.addTab("", new JPanel());
        tabbedPane.setTabComponentAt(1, new TabIcon(UiUtils.GRAPHQL_ICON));
        tabbedPane.addTab("", new JPanel());
        tabbedPane.setTabComponentAt(2, new TabIcon(UiUtils.WEB_SOCKET_ICON));
        tabbedPane.addTab("", new JPanel());
        tabbedPane.setTabComponentAt(3, new TabIcon(UiUtils.MCP_ICON));
        tabbedPane.addTab("", settingsPanel);
        tabbedPane.setTabComponentAt(4, new TabIcon(UiUtils.SETTINGS_ICON));
        this.add(tabbedPane, "grow");
    }

    public class HttpPanel extends JPanel implements Struct, Translatable {

        private final JLabel titleLabel = new JLabel(UiUtils.APP_NAME);
        @Getter
        private final TextField searchField = new TextField();
        @Getter
        private final JTabbedPane tabbedPane = new JTabbedPane();
        @Getter
        private final TreeCollections treeCollections = new TreeCollections();
        @Getter
        private final TreeEnvironment treeEnviroment = new TreeEnvironment();
        @Getter
        private final TreePanelHistory treePanelHistory = new TreePanelHistory();
        @Getter
        private final CodePanel codePanel = new CodePanel();

        public HttpPanel() {
            this.build();
            LangManager.register(this);
        }

        @Override
        public void initComponents() {
        }

        @Override
        public void configureStyle() {
            Style.setLayout(this, new MigLayout("fill, insets 13", "[grow]", "[]10[]10[grow]"));
            Style.setTabArc(tabbedPane);
            searchField.setPlaceholder(LangManager.text("sidebar.httpPanel.searchField.placeholder.text"));
        }

        @Override
        public void compose() {
            tabbedPane.addTab("", treeCollections);
            tabbedPane.setTabComponentAt(0, new TabIcon(UiUtils.FOLDER_ICON, 43));
            tabbedPane.addTab("", treeEnviroment);
            tabbedPane.setTabComponentAt(1, new TabIcon(UiUtils.STACK_ICON, 43));
            tabbedPane.addTab("", treePanelHistory);
            tabbedPane.setTabComponentAt(2, new TabIcon(UiUtils.HISTORY_ICON, 43));
            tabbedPane.addTab("", codePanel);
            tabbedPane.setTabComponentAt(3, new TabIcon(UiUtils.CODE_ICON, 43));

            this.add(titleLabel, "wrap");
            this.add(searchField, "growx, wrap, h 30!");
            this.add(tabbedPane, "grow, span");
        }

        @Override
        public void updateText() {
            searchField.setPlaceholder(LangManager.text("sidebar.httpPanel.searchField.placeholder.text"));
        }

        public class CodePanel extends JPanel implements Struct, Translatable {
            private final int FONT_SIZE = 13;
            private JPanel cbmContainer;
            private final JComboBox<String> comboBox = new JComboBox<>(new String[] { "Shell - cURL" });

            private JPanel border;
            private JPanel header;
            private final JLabel label = new JLabel(LangManager.text("sidebar.httpPanel.codePanel.label.text"));
            @Getter
            private final JButton copyButton = new JButton(UiUtils.COPY_ICON);
            @Getter
            private final SyntaxEditor editor = new SyntaxEditor(SyntaxConstants.SYNTAX_STYLE_NONE);

            private CodePanel() {
                this.build();
            }

            @Override
            public void initComponents() {
                cbmContainer = new JPanel(new MigLayout("fill, insets 3"));
                border = new JPanel(new MigLayout("fill, insets 1", "[]", "[]1[]"));
                header = new JPanel(new MigLayout("fillx, aligny 50%, insets 0", "10[]push[]5"));
            }

            @Override
            public void configureStyle() {
                Style.setLayout(this, new MigLayout("fillx, insets 10 0 10 0", "[fill]", "[]5[]"));
                Style.setRoundComponent(cbmContainer);
                Style.setFontSize(comboBox, FONT_SIZE);
                Style.setCursor(comboBox, Cursor.HAND_CURSOR);
                Style.setBackgroundColor(border, Colors.SECONDARY_COLOR);
                Style.setRoundComponent(border);
                Style.setRoundComponent(header);
                Style.setFontSize(label, FONT_SIZE);
                Style.setTransparent(copyButton);
                Style.setToolTip(copyButton, LangManager.text("app.panel.copyButton.toolTip.text"));
                Style.setCursor(copyButton, Cursor.HAND_CURSOR);
            }

            @Override
            public void compose() {
                header.add(label);
                header.add(copyButton);
                border.add(header, "h 35!, grow, wrap");
                border.add(editor, "h 200!, grow");
                this.add(comboBox, "h 35!, wrap");
                this.add(border);
            }

            @Override
            public void updateText() {
                Style.setLabelText(label, LangManager.text("sidebar.httpPanel.codePanel.label.text"));
                Style.setToolTip(copyButton, LangManager.text("app.panel.copyButton.toolTip.text"));
            }

        }
    }

    public class SettingsPanel extends JPanel implements Struct, Translatable {
        private final JLabel titleLabel = new JLabel(LangManager.text("sidebar.settingsPanel.titleLabel.text"));
        private final JLabel languageLabel = new JLabel(LangManager.text("sidebar.settingsPanel.languageLabel.text"));
        @Getter
        private final JComboBox<String> selectLanguage = new JComboBox<>(
                new DefaultComboBoxModel<>(
                        Arrays.stream(Lang.values())
                                .map(lang -> LangManager.text(lang.getBundleKey()))
                                .toArray(String[]::new)));
        private final JLabel themeLabel = new JLabel(LangManager.text("sidebar.settingsPanel.themeLabel.text"));
        private final JLabel backgroundLabel = new JLabel(
                LangManager.text("sidebar.settingsPanel.backgroundLabel.text"));
        private JPanel themesContainer;
        private final JButton systemThemeButton = new JButton(UiUtils.SYSTEM_ICON);
        private final JButton darkThemeButton = new JButton(UiUtils.MOON_ICON);
        private final JButton lightThemeButton = new JButton(UiUtils.SUN_ICON);

        private final JLabel accentColorLabel = new JLabel(
                LangManager.text("sidebar.settingsPanel.accentColorLabel.text"));
        private JPanel radioBtnContainer;
        private final JRadioButton green = new JRadioButton();
        private final JRadioButton teal = new JRadioButton();
        private final JRadioButton blue = new JRadioButton();
        private final JRadioButton indigo = new JRadioButton();
        private final JRadioButton purple = new JRadioButton();
        private final JRadioButton red = new JRadioButton();

        private final JLabel shortcutsLabel = new JLabel("Shortcuts");

        @Getter
        private List<JButton> themeButtons;
        @Getter
        private List<JRadioButton> accentColoRadioButtons;

        private SettingsPanel() {
            this.build();
            themeButtons = List.of(systemThemeButton, darkThemeButton, lightThemeButton);
            accentColoRadioButtons = List.of(green, teal, blue, indigo, purple, red);
            LangManager.register(this);
        }

        @Override
        public void initComponents() {
            radioBtnContainer = new JPanel(new MigLayout("fillx, insets 0 15 0 0", "[]", "[]0[]0[]0[]0[]0[]"));
            themesContainer = new JPanel(new MigLayout("fillx, insets 0 15 0 0", "[]", "0[]0[]0[]0"));
        }

        @Override
        public void configureStyle() {
            Style.setLayout(this, new MigLayout("fillx, insets 13", "[grow]"));
            Style.setFontSize(languageLabel, 12);
            Style.setInsets(selectLanguage, 0, 15, 0, 0);
            Style.setCursor(selectLanguage, Cursor.HAND_CURSOR);
            Style.setFontSize(selectLanguage, 13.5f);
            Style.setInsets(accentColorLabel, 0, 15, 0, 0);
            Style.setFontSize(backgroundLabel, 12);
            Style.setInsets(backgroundLabel, 0, 15, 0, 0);
            Style.setFontSize(accentColorLabel, 12);
            Style.setTransparent(radioBtnContainer);
            green.putClientProperty(FlatClientProperties.STYLE, "icon.selectedBackground:#42BC80");
            teal.putClientProperty(FlatClientProperties.STYLE, "icon.selectedBackground:#47B9A5");
            blue.putClientProperty(FlatClientProperties.STYLE, "icon.selectedBackground:#5876F5");
            indigo.putClientProperty(FlatClientProperties.STYLE, "icon.selectedBackground:#6f53f1");
            purple.putClientProperty(FlatClientProperties.STYLE, "icon.selectedBackground:#a839f7");
            red.putClientProperty(FlatClientProperties.STYLE, "icon.selectedBackground:#e24548");
            Style.setCursor(systemThemeButton, Cursor.HAND_CURSOR);
            Style.setBackgroundColor(systemThemeButton, Colors.SECONDARY_COLOR);
            Style.setToolTip(systemThemeButton,
                    LangManager.text("sidebar.settingsPanel.systemThemeButton.toolTip.text"));
            Style.setUndecoratedButton(systemThemeButton);
            Style.setCursor(darkThemeButton, Cursor.HAND_CURSOR);
            Style.setBackgroundColor(darkThemeButton, Colors.SECONDARY_COLOR);
            Style.setToolTip(darkThemeButton, LangManager.text("sidebar.settingsPanel.darkThemeButton.toolTip.text"));
            Style.setUndecoratedButton(darkThemeButton);
            Style.setCursor(lightThemeButton, Cursor.HAND_CURSOR);
            Style.setBackgroundColor(lightThemeButton, Colors.SECONDARY_COLOR);
            Style.setToolTip(lightThemeButton, LangManager.text("sidebar.settingsPanel.lightThemeButton.toolTip.text"));
            Style.setUndecoratedButton(lightThemeButton);
        }

        @Override
        public void compose() {
            this.add(titleLabel, "wrap");
            this.add(languageLabel, "wrap");
            this.add(selectLanguage, "w 120!, h 25!, wrap");

            this.add(separator(), "grow, wrap");

            this.add(themeLabel, "wrap");
            this.add(backgroundLabel, "wrap");
            themesContainer.add(systemThemeButton, "grow");
            themesContainer.add(darkThemeButton, "grow");
            themesContainer.add(lightThemeButton, "grow");
            this.add(themesContainer, "h 25!, grow, wrap");
            this.add(accentColorLabel, "wrap");
            radioBtnContainer.add(green, "grow");
            radioBtnContainer.add(teal, "grow");
            radioBtnContainer.add(blue, "grow");
            radioBtnContainer.add(indigo, "grow");
            radioBtnContainer.add(purple, "grow");
            radioBtnContainer.add(red, "grow");
            this.add(radioBtnContainer, "h 20!, grow, wrap");

            this.add(separator(), "grow, wrap");

            this.add(shortcutsLabel, "wrap");
        }

        @Override
        public void updateText() {
            Style.setLabelText(titleLabel, LangManager.text("sidebar.settingsPanel.titleLabel.text"));
            Style.setLabelText(languageLabel, LangManager.text("sidebar.settingsPanel.languageLabel.text"));
            Style.setLabelText(themeLabel, LangManager.text("sidebar.settingsPanel.themeLabel.text"));
            Style.setLabelText(backgroundLabel, LangManager.text("sidebar.settingsPanel.backgroundLabel.text"));
            Style.setLabelText(accentColorLabel, LangManager.text("sidebar.settingsPanel.accentColorLabel.text"));
            // Style.setLabelText(shortcutsLabel,
            // LangManager.text("sidebar.settingsPanel.interceptorLabel.text"));
            Style.setToolTip(systemThemeButton,
                    LangManager.text("sidebar.settingsPanel.systemThemeButton.toolTip.text"));
            Style.setToolTip(darkThemeButton, LangManager.text("sidebar.settingsPanel.darkThemeButton.toolTip.text"));
            Style.setToolTip(lightThemeButton, LangManager.text("sidebar.settingsPanel.lightThemeButton.toolTip.text"));
            selectLanguage.setModel(new DefaultComboBoxModel<>(
                    Arrays.stream(Lang.values())
                            .map(lang -> LangManager.text(lang.getBundleKey()))
                            .toArray(String[]::new)));

            selectLanguage.setSelectedIndex(LangManager.getLang().ordinal());
        }

        private JSeparator separator() {
            final JSeparator separator = new JSeparator();
            Style.setTextColor(separator, Colors.SECONDARY_COLOR);
            return separator;
        }

    }
}
