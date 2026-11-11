package de.htwberlin.einkaufsliste.einkaufsliste;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@SpringBootTest
class EinkaufslisteServiceTest {

    @Autowired
    private EinkaufslisteService service;

    @MockitoBean
    private EinkaufslisteneintragRepository repository;

    private List<Einkaufslisteneintrag> eintraege;

    @BeforeEach
    void beispielEintraege() {
        eintraege = new ArrayList<>(List.of(
            new Einkaufslisteneintrag(1L, "Spaghetti", 1, "200 g", 1, false, "demo"),
            new Einkaufslisteneintrag(2L, "Eier", 1, null, 2, false, "demo"),
            new Einkaufslisteneintrag(3L, "Milch", 2, null, 3, false, "demo"),
            new Einkaufslisteneintrag(4L, "Linsen", 1, "250 g", 4, true, "demo")));
        doReturn(eintraege).when(repository).findAll();
        doReturn(eintraege).when(repository).findAllByOrderByReihenfolgeAsc();
        doAnswer(aufruf -> aufruf.getArgument(0)).when(repository).save(any(Einkaufslisteneintrag.class));
    }

    @Test
    void liefertEintraegeAusDemRepository() {
        assertThat(service.findAlle()).extracting(Einkaufslisteneintrag::getId).containsExactly(1L, 2L, 3L, 4L);
    }

    @Test
    void uebernimmtNeueReihenfolge() {
        List<Einkaufslisteneintrag> ergebnis = service.reihenfolgeAendern(List.of(3L, 1L, 2L, 4L));

        assertThat(ergebnis).extracting(Einkaufslisteneintrag::getId).containsExactly(3L, 1L, 2L, 4L);
        assertThat(ergebnis).extracting(Einkaufslisteneintrag::getReihenfolge).containsExactly(1, 2, 3, 4);
        verify(repository).saveAll(any());
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

    @Test
    void legtEintragAmEndeDerListeAn() {
        Einkaufslisteneintrag eintrag = service.anlegen(new NeuerEintrag("  Butter ", 2));

        assertThat(eintrag.getName()).isEqualTo("Butter");
        assertThat(eintrag.getStueckzahl()).isEqualTo(2);
        assertThat(eintrag.getReihenfolge()).isEqualTo(5);
        assertThat(eintrag.getRezeptmenge()).isNull();
        assertThat(eintrag.isErledigt()).isFalse();
    }

    @Test
    void legtErstenEintragAufPositionEinsAn() {
        doReturn(List.of()).when(repository).findAll();

        assertThat(service.anlegen(new NeuerEintrag("Butter", 1)).getReihenfolge()).isEqualTo(1);
    }

    @Test
    void schlaegtPassendeNamenVor() {
        assertThat(service.vorschlaege("li")).containsExactly("Linsen");
        assertThat(service.vorschlaege("  MI ")).containsExactly("Milch");
    }

    @Test
    void schlaegtGleicheNamenNurEinmalVor() {
        eintraege.add(new Einkaufslisteneintrag(5L, "milch", 1, null, 5, false, "demo"));

        assertThat(service.vorschlaege("milch")).containsExactly("Milch");
    }

    @Test
    void schlaegtOhneSuchtextAlleNamenAlphabetischVor() {
        assertThat(service.vorschlaege("")).containsExactly("Eier", "Linsen", "Milch", "Spaghetti");
    }

    @Test
    void aendertNameStueckzahlUndErledigtAberNichtDieRezeptmenge() {
        doReturn(Optional.of(eintraege.getFirst())).when(repository).findById(1L);

        Einkaufslisteneintrag eintrag = service.aendern(1L, new EintragAenderung(" Vollkornspaghetti ", 2, true))
            .orElseThrow();

        assertThat(eintrag.getName()).isEqualTo("Vollkornspaghetti");
        assertThat(eintrag.getStueckzahl()).isEqualTo(2);
        assertThat(eintrag.isErledigt()).isTrue();
        assertThat(eintrag.getRezeptmenge()).isEqualTo("200 g");
    }

    @Test
    void aendernMeldetUnbekannteId() {
        doReturn(Optional.empty()).when(repository).findById(99L);

        assertThat(service.aendern(99L, new EintragAenderung("Butter", 1, false))).isEmpty();
    }

    @Test
    void loeschtVorhandenenEintrag() {
        doReturn(true).when(repository).existsById(2L);

        assertThat(service.loeschen(2L)).isTrue();
        verify(repository).deleteById(2L);
    }

    @Test
    void loeschenMeldetUnbekannteId() {
        doReturn(false).when(repository).existsById(99L);

        assertThat(service.loeschen(99L)).isFalse();
        verify(repository, never()).deleteById(99L);
    }
}
