package de.htwberlin.einkaufsliste.rezept;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RezeptServiceTest {

    private final RezeptService rezeptService = new RezeptService();

    @Test
    void findeNachIdLiefertVorhandenesRezept() {
        Optional<Rezept> rezept = rezeptService.findeNachId(1L);

        assertTrue(rezept.isPresent());
        assertEquals("Spaghetti Carbonara", rezept.get().getTitel());
    }

    @Test
    void findeNachIdLiefertLeerBeiUnbekannterId() {
        assertTrue(rezeptService.findeNachId(999L).isEmpty());
    }
}
