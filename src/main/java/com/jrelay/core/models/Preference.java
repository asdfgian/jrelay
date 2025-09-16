package com.jrelay.core.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents user preferences for language, theme, and accent color.
 * <p>
 * This class stores customizable settings related to the user interface
 * experience,
 * such as language selection, theme mode, and accent color preference.
 * <p>
 * Defaults are: {@code Lang.ENG}, {@code Theme.DARK}, and
 * {@code AccentColor.GREEN}.
 * <p>
 * Includes enums for supported languages and themes.
 * <p>
 * Lombok annotations are used to automatically generate getters, setters,
 * a constructor with all arguments, and a {@code toString()} implementation.
 *
 * @author @ASDG14N
 * @since 02-08-2025
 */
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Preference {

    private Lang lang;
    private Theme theme;
    private AccentColor accentColor;

    /**
     * Enum for supported interface languages.
     */
    public enum Lang {
        EN("item.en.text"),
        ES("item.es.text"),
        FR("item.fr.text"),
        DE("item.de.text"),
        IT("item.it.text"),
        PT("item.pt.text"),
        ZH("item.zh.text"),
        JA("item.ja.text"),
        KO("item.ko.text");

        @Getter
        private final String bundleKey;

        Lang(String bundleKey) {
            this.bundleKey = bundleKey;
        }
    }

    /**
     * Enum for available UI themes.
     */
    public enum Theme {
        SYS, DARK, LIGHT
    }

    /**
     * Enum representing the available accent colors with their corresponding hex
     * values.
     */
    public enum AccentColor {
        GREEN("#42BC80"),
        TEAL("#47B9A5"),
        BLUE("#5876F5"),
        INDIGO("#6f53f1"),
        PURPLE("#a839f7"),
        RED("#e24548");

        @Getter
        private final String color;

        AccentColor(String color) {
            this.color = color;
        }
    }

    /**
     * Constructs a {@code Preference} with default values.
     */
    public Preference(Lang lang, Theme theme) {
        this(lang, theme, AccentColor.BLUE);
    }
}
