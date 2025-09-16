package com.jrelay.ui.shared.styles;

import java.awt.Color;

import com.jrelay.core.models.Preference.AccentColor;


/**
 * Defines color constants and accent color options used throughout the UI.
 * <p>
 * This class provides a central place for managing static color definitions,
 * including HTTP method colors, icon color, theme-related colors, and
 * user-selected
 * accent colors.
 * <p>
 * The {@link AccentColor} enum defines a palette of accent color options,
 * each associated with a hexadecimal color code.
 * <p>
 * This class is intended to support a consistent and theme-aware user
 * interface.
 *
 * @author @ASDG14N
 * @since 02-08-2025
 */
public class Colors {

    private Colors() {
    }

    /**
     * The currently selected accent color.
     */
    public static AccentColor ACCENT_COLOR;

    /**
     * Default color used for icons.
     */
    public static final Color ICON_COLOR = Color.decode("#a3a3a3");

    /**
     * Secondary background color used across the UI.
     */
    public static Color SECONDARY_COLOR = Color.decode("#242426");

    /**
     * Default background color for text fields.
     */
    public static Color TEXT_FIELD_COLOR = Color.decode("#242426");

    /**
     * Color associated with HTTP GET requests.
     */
    public static final Color GET_COLOR = Color.decode("#10b981");

    /**
     * Color associated with HTTP POST requests.
     */
    public static Color POST_COLOR = Color.decode("#eab308");

    /**
     * Color associated with HTTP PUT requests.
     */
    public static Color PUT_COLOR = Color.decode("#0ea5e9");

    /**
     * Color associated with HTTP PATCH requests.
     */
    public static Color PATCH_COLOR = Color.decode("#8b5cf6");

    /**
     * Color associated with HTTP DELETE requests.
     */
    public static Color DELETE_COLOR = Color.decode("#f43f5e");

    public static Color WARNING_COLOR = Color.decode("#ff746c");
}
