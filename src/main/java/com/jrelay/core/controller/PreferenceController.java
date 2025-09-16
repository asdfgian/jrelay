package com.jrelay.core.controller;

import com.jrelay.core.models.Preference;
import com.jrelay.core.models.Preference.AccentColor;
import com.jrelay.core.models.Preference.Lang;
import com.jrelay.core.models.Preference.Theme;
import com.jrelay.core.repository.PreferenceRepository;
import com.jrelay.core.utils.i18n.LangManager;
import com.jrelay.ui.shared.styles.Colors;

public class PreferenceController {
    private final PreferenceRepository repository;
    private Preference preference;

    public PreferenceController() {
        this.repository = new PreferenceRepository("preference.json");
        init();
    }

    private void init() {
        preference = repository.getPreference();
        Colors.ACCENT_COLOR = preference.getAccentColor();
        LangManager.loadBundle(preference.getLang());
    }

    public void setLanguage(Lang lang) {
        preference.setLang(lang);
        repository.updatePreference(preference);
    }

    public void setColor(AccentColor accentColor) {
        preference.setAccentColor(accentColor);
        repository.updatePreference(preference);
    }

    public void setTheme(Theme theme) {
        preference.setTheme(theme);
        repository.updatePreference(preference);
    }

    public Theme getTheme(){
        return preference.getTheme();
    }
}
