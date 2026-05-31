package br.capacita.contatos.util;

import java.util.prefs.Preferences;

public class ThemeManager {
    private static final Preferences prefs =
            Preferences.userNodeForPackage(ThemeManager.class);

    public static String getCurrentTheme() {
        boolean darkMode = prefs.getBoolean("darkMode", true);

        return darkMode
                ? "/styles/dark.css"
                : "/styles/light.css";
    }

    public static void setDarkMode(boolean darkMode) {
        prefs.putBoolean("darkMode", darkMode);
    }
}
