package de.htwberlin.einkaufsliste.einkaufsliste;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EinkaufslisteServiceTest {

    private final EinkaufslisteService service = new EinkaufslisteService();

    @Test
    void liefertEintraegeNachReihenfolgeSortiert() {
        List<Integer> reihenfolgen = service.findAlle().stream()
            .map(Einkaufslisteneintrag::getReihenfolge)
            .toList();

        assertThat(reihenfolgen).isSorted();
    }

    @Test
    void uebernimmtNeueReihenfolge() {
        List<Einkaufslisteneintrag> ergebnis = service.reihenfolgeAendern(List.of(3L, 1L, 2L, 4L));

        assertThat(ergebnis).extracting(Einkaufslisteneintrag::getId).containsExactly(3L, 1L, 2L, 4L);
        assertThat(service.findAlle()).extracting(Einkaufslisteneintrag::getId).containsExactly(3L, 1L, 2L, 4L);
    }

    @Test
    void lehntUnbekannteIdAb() {
        assertThatThrownBy(() -> service.reihenfolgeAendern(List.of(1L, 2L, 3L, 99L)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void lehntFehlendeIdAb() {
        assertThatThrownBy(() -> service.reihenfolgeAendern(List.of(1L, 2L, 3L)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void lehntDoppelteIdAb() {
        assertThatThrownBy(() -> service.reihenfolgeAendern(List.of(1L, 1L, 2L, 3L)))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
