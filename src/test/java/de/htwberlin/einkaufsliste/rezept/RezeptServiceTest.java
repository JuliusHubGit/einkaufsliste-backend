package de.htwberlin.einkaufsliste.rezept;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RezeptServiceTest {

    private final RezeptService rezeptService = new RezeptService();

    @Test
    void loeschtVorhandenesRezept() {
        boolean geloescht = rezeptService.loeschen(1L);

        assertThat(geloescht).isTrue();
        assertThat(rezeptService.findAlle()).extracting(Rezept::getId).doesNotContain(1L);
    }

    @Test
    void meldetUnbekannteIdBeimLoeschen() {
        int vorher = rezeptService.findAlle().size();

        boolean geloescht = rezeptService.loeschen(99L);

        assertThat(geloescht).isFalse();
        assertThat(rezeptService.findAlle()).hasSize(vorher);
    }
}
