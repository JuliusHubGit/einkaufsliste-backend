package de.htwberlin.einkaufsliste.fehler;

import java.util.Map;

/**
 * Fachliche Eingabefehler, die Bean Validation nicht abdeckt; der FehlerHandler macht daraus
 * eine 400-Antwort mit denselben Feldfehlern wie bei der Validierung.
 */
public class FeldfehlerException extends RuntimeException {

    private final Map<String, String> feldfehler;

    public FeldfehlerException(Map<String, String> feldfehler) {
        super("Ungueltige Eingabe: " + feldfehler.keySet());
        this.feldfehler = feldfehler;
    }

    public Map<String, String> getFeldfehler() {
        return feldfehler;
    }
}
