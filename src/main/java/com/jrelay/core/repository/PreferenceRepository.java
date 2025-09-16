package com.jrelay.core.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jrelay.core.models.Preference;
import com.jrelay.core.os.OsManager;

import lombok.Getter;

public class PreferenceRepository extends JsonRepository<Preference> {
    private final OsManager osManager;

    @Getter
    private final Preference preference;

    public PreferenceRepository(String path) {
        super(path, new TypeReference<>() {
        });
        this.osManager = OsManager.getInstance();
        this.preference = loadOrDefault(createDefaultPreference());
    }

    public void updatePreference(Preference arg) {
        preference.setLang(arg.getLang());
        preference.setTheme(arg.getTheme());
        preference.setAccentColor(arg.getAccentColor());
        setEntity(preference);
    }

    private Preference createDefaultPreference() {
        return new Preference(osManager.getLanguage(), osManager.getTheme());
    }
}
