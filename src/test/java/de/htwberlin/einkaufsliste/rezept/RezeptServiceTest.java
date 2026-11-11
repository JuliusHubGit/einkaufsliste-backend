package de.htwberlin.einkaufsliste.rezept;

import de.htwberlin.einkaufsliste.fehler.FeldfehlerException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@SpringBootTest
class RezeptServiceTest {

    @Autowired
    private RezeptService rezeptService;

    @MockitoBean
    private RezeptRepository repository;

    @Test
    void findAlleLiefertRezepteAusDemRepository() {
        doReturn(List.of(new Rezept(1L, "Spaghetti Carbonara", 2, Schwierigkeit.NORMAL, "demo"),
            new Rezept(2L, "Linsensuppe", 4, Schwierigkeit.NORMAL, "demo"))).when(repository).findAll();

        assertThat(rezeptService.findAlle()).extracting(Rezept::getTitel)
            .containsExactly("Spaghetti Carbonara", "Linsensuppe");
    }

    @Test
    void findeNachIdLiefertVorhandenesRezept() {
        doReturn(Optional.of(new Rezept(1L, "Spaghetti Carbonara", 2, Schwierigkeit.NORMAL, "demo"))).when(repository).findById(1L);

        assertThat(rezeptService.findeNachId(1L))
            .map(Rezept::getTitel)
            .contains("Spaghetti Carbonara");
    }

    @Test
    void findeNachIdLiefertLeerBeiUnbekannterId() {
        doReturn(Optional.empty()).when(repository).findById(999L);

        assertThat(rezeptService.findeNachId(999L)).isEmpty();
    }

    @Test
    void legtRezeptMitZutatenAn() {
        doAnswer(aufruf -> aufruf.getArgument(0)).when(repository).save(any(Rezept.class));

        Rezept rezept = rezeptService.anlegen(new NeuesRezept(" Pfannkuchen ", 2, Schwierigkeit.EINFACH,
            List.of(new NeuesRezept.NeueZutat(" Mehl ", 200, " g ")), null));

        assertThat(rezept.getTitel()).isEqualTo("Pfannkuchen");
        assertThat(rezept.getSchwierigkeit()).isEqualTo(Schwierigkeit.EINFACH);
        assertThat(rezept.getBesitzer()).isEqualTo("demo");
        assertThat(rezept.getZutaten()).singleElement().satisfies(zutat -> {
            assertThat(zutat.getName()).isEqualTo("Mehl");
            assertThat(zutat.getEinheit()).isEqualTo("g");
            assertThat(zutat.getRezept()).isSameAs(rezept);
        });
    }

    @Test
    void legtRezeptOhneZutatenAn() {
        doAnswer(aufruf -> aufruf.getArgument(0)).when(repository).save(any(Rezept.class));

        Rezept rezept = rezeptService.anlegen(new NeuesRezept("Toast", 1, Schwierigkeit.NORMAL, null, null));

        assertThat(rezept.getZutaten()).isEmpty();
        assertThat(rezept.getSchritte()).isEmpty();
    }

    @Test
    void legtSchritteInReihenfolgeMitIhrenZutatenAn() {
        doAnswer(aufruf -> aufruf.getArgument(0)).when(repository).save(any(Rezept.class));

        Rezept rezept = rezeptService.anlegen(new NeuesRezept("Pfannkuchen", 2, Schwierigkeit.EINFACH,
            List.of(new NeuesRezept.NeueZutat("Mehl", 200, "g"), new NeuesRezept.NeueZutat("Eier", 2, "Stück")),
            List.of(new NeuesRezept.NeuerSchritt(" Mehl und Eier verrühren ", List.of(0, 1, 1)),
                new NeuesRezept.NeuerSchritt("Ausbacken", null))));

        assertThat(rezept.getSchritte()).extracting(Schritt::getPosition).containsExactly(1, 2);
        Schritt erster = rezept.getSchritte().getFirst();
        assertThat(erster.getBeschreibung()).isEqualTo("Mehl und Eier verrühren");
        assertThat(erster.getRezept()).isSameAs(rezept);
        assertThat(erster.getZutaten()).extracting(verweis -> verweis.getZutat().getName())
            .containsExactly("Mehl", "Eier");
        assertThat(erster.getZutaten()).allSatisfy(verweis -> {
            assertThat(verweis.getSchritt()).isSameAs(erster);
            assertThat(verweis.getZutat().getRezept()).isSameAs(rezept);
        });
        assertThat(rezept.getSchritte().get(1).getZutaten()).isEmpty();
    }

    @Test
    void lehntVerweisAufUnbekannteZutatAb() {
        NeuesRezept neuesRezept = new NeuesRezept("Pfannkuchen", 2, Schwierigkeit.EINFACH,
            List.of(new NeuesRezept.NeueZutat("Mehl", 200, "g")),
            List.of(new NeuesRezept.NeuerSchritt("Verrühren", List.of(0)),
                new NeuesRezept.NeuerSchritt("Ausbacken", List.of(1))));

        assertThatThrownBy(() -> rezeptService.anlegen(neuesRezept))
            .isInstanceOf(FeldfehlerException.class)
            .extracting(e -> ((FeldfehlerException) e).getFeldfehler())
            .isEqualTo(Map.of("schritte[1].zutaten", "Der Schritt verweist auf eine unbekannte Zutat"));
        verify(repository, never()).save(any());
    }

    @Test
    void loeschtVorhandenesRezept() {
        doReturn(true).when(repository).existsById(1L);

        assertThat(rezeptService.loeschen(1L)).isTrue();
        verify(repository).deleteById(1L);
    }

    @Test
    void meldetUnbekannteIdBeimLoeschen() {
        doReturn(false).when(repository).existsById(99L);

        assertThat(rezeptService.loeschen(99L)).isFalse();
        verify(repository, never()).deleteById(99L);
    }
}
