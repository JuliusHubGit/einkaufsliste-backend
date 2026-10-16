package de.htwberlin.einkaufsliste.fehler;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Wandelt Validierungsfehler in eine 400-Antwort mit Feldfehlern um:
 * {@code {"feldfehler": {"name": "Bitte einen Namen eingeben"}}}
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
}
