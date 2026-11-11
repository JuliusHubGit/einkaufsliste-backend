package de.htwberlin.einkaufsliste.einkaufsliste;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Reiner Unit-Test ohne Spring: die Normalisierung, auf der der Abgleich beim Uebernehmen beruht.
 */
class NamenTest {

    @Test
    void trimmtUndSchreibtKlein() {
        assertThat(Namen.normalisieren("  Spaghetti ")).isEqualTo("spaghetti");
    }

    @Test
    void behandeltUmlauteWieKleinbuchstaben() {
        assertThat(Namen.normalisieren("ÄPFEL")).isEqualTo("äpfel");
    }

    @Test
    void machtAusNullEinenLeerenNamen() {
        assertThat(Namen.normalisieren(null)).isEmpty();
    }

    @Test
    void laesstLeerzeichenImNamenStehen() {
        assertThat(Namen.normalisieren("Rote  Linsen")).isEqualTo("rote  linsen");
    }
}
