package com.jrelay.core.utils.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.jrelay.core.models.Preference.Lang;
import com.jrelay.ui.shared.utils.template.Translatable;

import lombok.Getter;

/**
 * Manages the loading and retrieval of localized text resources for
 * internationalization.
 * <p>
 * Uses {@link ResourceBundle} to load language-specific resource files and
 * provides
 * access to localized strings by key.
 * <p>
 * The resource bundles must follow the naming convention:
 *
 * <pre>
 * i18n.Bundle_LANG
 * </pre>
 * <p>
 * where {@code LANG} corresponds to the specified {@link Lang} enum value.
 *
 * @author ASDFG14N
 * @since 14-08-2025
 */

public class LangManager {

    private LangManager() {
    }

    private static ResourceBundle bundle;
    @Getter
    private static Lang lang;

    private static final List<Translatable> listeners = new ArrayList<>();

    /**
     * Loads the resource bundle for the specified language.
     *
     * @param newLang the {@link Lang} enum value representing the language to load
     */
    public static void loadBundle(Lang newLang) {
        LangManager.lang = newLang;
        bundle = ResourceBundle.getBundle("i18n.Bundle_" + newLang);
        notifyListeners();
    }

    /**
     * Retrieves a localized text string for the given key from the currently loaded
     * bundle.
     *
     * @param key the key for the desired string
     * @return the localized string associated with the given key
     * @throws MissingResourceException if the key is not found in the
     *                                  bundle
     */
    public static String text(String key) {
        return bundle.getString(key);
    }

    /**
     * Registers a {@link Translatable} component to the listeners list.
     * <p>
     * Any component that implements {@link Translatable} and is registered here
     * will automatically receive text updates when the application language
     * changes.
     *
     * @param t the {@link Translatable} instance to register, must not be
     *          {@code null}
     */
    public static void register(Translatable t) {
        listeners.add(t);
    }

    /**
     * Notifies all registered {@link Translatable} components that the language has
     * changed.
     * <p>
     * This method iterates through the list of registered listeners and calls
     * {@link Translatable#updateText()} on each, ensuring their displayed text
     * is refreshed according to the active language bundle.
     *
     */
    public static void notifyListeners() {
        for (Translatable t : listeners) {
            t.updateText();
        }
    }
}
