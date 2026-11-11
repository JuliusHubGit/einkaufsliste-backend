package de.htwberlin.einkaufsliste.fehler;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.exc.InvalidFormatException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Wandelt Validierungsfehler in eine 400-Antwort mit Feldfehlern um:
 * {@code {"feldfehler": {"name": "Bitte einen Namen eingeben"}}}
 * Dasselbe gilt fuer Werte, die nicht in den Feldtyp passen (z. B. eine unbekannte Schwierigkeit).
 */
@RestControllerAdvice
public class FehlerHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Map<String, String>>> validierungFehlgeschlagen(
            MethodArgumentNotValidException e) {
        Map<String, String> feldfehler = new LinkedHashMap<>();
        for (FieldError fehler : e.getBindingResult().getFieldErrors()) {
            feldfehler.putIfAbsent(fehler.getField(), fehler.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(Map.of("feldfehler", feldfehler));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Map<String, String>>> unlesbarerInhalt(HttpMessageNotReadableException e) {
        Map<String, String> feldfehler = new LinkedHashMap<>();
        if (e.getCause() instanceof InvalidFormatException formatFehler) {
            String feld = formatFehler.getPath().stream()
                .map(JacksonException.Reference::getPropertyName)
                .filter(name -> name != null)
                .reduce((erstes, letztes) -> letztes)
                .orElse("anfrage");
            feldfehler.put(feld, "Ungültiger Wert");
        } else {
            feldfehler.put("anfrage", "Die Anfrage konnte nicht gelesen werden");
        }
        return ResponseEntity.badRequest().body(Map.of("feldfehler", feldfehler));
    }

    @ExceptionHandler(FeldfehlerException.class)
    public ResponseEntity<Map<String, Map<String, String>>> fachlicherFehler(FeldfehlerException e) {
        return ResponseEntity.badRequest().body(Map.of("feldfehler", e.getFeldfehler()));
    }
}
