package com.jrelay.ui.shared.styles;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.Window;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.jrelay.core.models.Preference.AccentColor;
import com.jrelay.core.models.Preference.Lang;
import com.jrelay.core.models.Preference.Theme;
import com.jrelay.core.utils.i18n.LangManager;
import com.jrelay.themes.DarkLaf;
import com.jrelay.themes.LightLaf;
import com.jrelay.ui.controllers.AppController;

public class Style {

    private Style() {
    }

    private static final int MIN_SCREEN_W = 800;
    private static final int MIN_SCREEN_H = 700;

    private static final int ARC = 10;

    public static void installLightTheme() {
        Colors.SECONDARY_COLOR = new Color(228, 228, 228);
        Colors.TEXT_FIELD_COLOR = new Color(228, 228, 228);
        System.setProperty("flatlaf.uiScale", "1.0");
        LightLaf.setup();
        setFont(LangManager.getLang());
    }

    public static void installDarkTheme() {
        FlatLaf.setGlobalExtraDefaults(Map.of("@accentColor", Colors.ACCENT_COLOR.getColor()));
        System.setProperty("flatlaf.uiScale", "1.0");
        DarkLaf.setup();
        setFont(LangManager.getLang());
    }

    public static void updateAccentColor(AccentColor accentColor) {
        AppController.prefController.setColor(accentColor);
        Colors.ACCENT_COLOR = accentColor;
        EventQueue.invokeLater(() -> {
            FlatAnimatedLafChange.showSnapshot();
            FlatLaf.setGlobalExtraDefaults(Map.of("@accentColor", Colors.ACCENT_COLOR.getColor()));
            if (FlatLaf.isLafDark())
                DarkLaf.setup();
            else
                LightLaf.setup();
            FlatLaf.updateUI();
            FlatAnimatedLafChange.hideSnapshotWithAnimation();
        });
    }

    public static void updateTheme(int id) {
        EventQueue.invokeLater(() -> {
            FlatAnimatedLafChange.showSnapshot();
            FlatLaf.setGlobalExtraDefaults(Map.of("@accentColor", Colors.ACCENT_COLOR.getColor()));
            switch (id) {
                case 0 -> {
                    Theme osTheme = AppController.osManager.getTheme();
                    if (osTheme == Theme.DARK) {
                        DarkLaf.setup();
                    } else {
                        LightLaf.setup();
                    }
                }
                case 1 -> DarkLaf.setup();
                case 2 -> LightLaf.setup();
                default -> System.out.println("");
            }
            FlatLaf.updateUI();
            FlatAnimatedLafChange.hideSnapshotWithAnimation();
        });
    }

    private static void setFont(Lang lang) {
        Font newFont;
        switch (lang) {
            case ZH, JA, KO -> newFont = new Font("Noto Sans CJK JP", Font.PLAIN, 13);
            default -> newFont = new Font("Segoe UI", Font.PLAIN, 15);
        }
        UIManager.put("defaultFont", newFont);
    }

    public static void updateFont(Lang lang) {
        setFont(lang);
        for (Window window : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(window);
        }
    }

    public static void setFrameMinSize(JFrame jFrame) {
        jFrame.setMinimumSize(new Dimension(MIN_SCREEN_W, MIN_SCREEN_H));
    }

    public static void setTransparent(AbstractButton btn) {
        btn.setContentAreaFilled(false);
    }

    public static void setTransparent(JComponent com) {
        com.setOpaque(false);
    }

    public static void setBackgroundColor(JComponent com, Color color) {
        com.setBackground(color);
    }

    public static void setRoundComponent(JComponent com) {
        com.putClientProperty(FlatClientProperties.STYLE, "arc: " + ARC);
    }

    public static void setRoundComponent(JComponent com, int arc) {
        com.putClientProperty(FlatClientProperties.STYLE, "arc: " + arc);
    }

    public static void setFontSize(JComponent com, float size) {
        com.setFont(com.getFont().deriveFont(size));
    }

    public static void setTextColor(JComponent com, Color color) {
        com.setForeground(color);
    }

    public static void setCursor(JComponent com, int cursor) {
        com.setCursor(Cursor.getPredefinedCursor(cursor));
    }

    public static void setIcon(AbstractButton button, Icon icon) {
        button.setIcon(icon);
    }

    public static void setLayout(JComponent com, LayoutManager layout) {
        com.setLayout(layout);
    }

    public static void setPlaceholder(JTextField textField, String placeholder) {
        textField.putClientProperty("JTextField.placeholderText", placeholder);
    }

    public static void setButtonText(AbstractButton button, String text) {
        button.setText(text);
    }

    public static void setToolTip(JComponent com, String toolTip) {
        com.setToolTipText(toolTip);
    }

    public static void setTabArc(JTabbedPane tabbedPane) {
        tabbedPane.putClientProperty(FlatClientProperties.STYLE, "tabArc: " + ARC);
    }

    public static void setTabTypeCard(JTabbedPane tabbedPane) {
        tabbedPane.putClientProperty(FlatClientProperties.STYLE, "tabType: card");
    }

    public static void setUndecoratedButton(AbstractButton btn) {
        btn.putClientProperty(FlatClientProperties.STYLE, "borderWidth:0;"
                + "focusWidth:0;"
                + "innerFocusWidth:0;");
    }

    public static void setLabelText(JLabel label, String text) {
        label.setText(text);
    }

    public static void setBold(JComponent com) {
        com.setFont(com.getFont().deriveFont(Font.BOLD));
    }

    public static void setInsets(JComponent com, int top, int left, int bottom, int right) {
        Border border = com.getBorder();
        Border margin = new EmptyBorder(top, left, bottom, right);
        com.setBorder(new CompoundBorder(border, margin));
    }

    public static Color getColorByMethod(String method) {
        return switch (method) {
            case "GET" -> Colors.GET_COLOR;
            case "POST" -> Colors.POST_COLOR;
            case "PUT" -> Colors.PUT_COLOR;
            case "PATCH" -> Colors.PATCH_COLOR;
            default -> Colors.DELETE_COLOR;
        };
    }

}
