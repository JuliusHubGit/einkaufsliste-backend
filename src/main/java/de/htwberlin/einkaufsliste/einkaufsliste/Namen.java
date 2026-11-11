package de.htwberlin.einkaufsliste.einkaufsliste;

import java.util.Locale;

/**
 * Normalform eines Namens: getrimmt und kleingeschrieben. Einzige Grundlage, um Zutaten mit
 * Einkaufslisteneintraegen abzugleichen (ADR 0001).
 */
public final class Namen {

    private Namen() {
    }

    public static String normalisieren(String name) {
        return name == null ? "" : name.trim().toLowerCase(Locale.GERMAN);
    }
}
