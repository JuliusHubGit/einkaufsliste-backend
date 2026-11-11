package de.htwberlin.einkaufsliste.rezept;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Prueft gegen die H2-Testdatenbank, dass Kaskaden und orphanRemoval keine Waisen hinterlassen.
 */
@SpringBootTest
class RezeptPersistenzTest {

    @Autowired
    private RezeptService rezeptService;

    @Autowired
    private RezeptRepository repository;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private TransactionTemplate transaktion;

    @BeforeEach
    void leereDatenbank() {
        repository.deleteAll();
    }

    private Rezept pfannkuchenAnlegen() {
        return rezeptService.anlegen(new NeuesRezept("Pfannkuchen", 2, Schwierigkeit.EINFACH,
            List.of(new NeuesRezept.NeueZutat("Mehl", 200, "g"), new NeuesRezept.NeueZutat("Eier", 2, "Stück")),
            List.of(new NeuesRezept.NeuerSchritt("Mehl und Eier verrühren", List.of(0, 1)),
                new NeuesRezept.NeuerSchritt("Ausbacken", List.of(0)))));
    }

    private int zeilen(String tabelle) {
        return jdbc.queryForObject("select count(*) from " + tabelle, Integer.class);
    }

    @Test
    void speichertZutatenSchritteUndVerweise() {
        pfannkuchenAnlegen();

        assertThat(zeilen("zutat")).isEqualTo(2);
        assertThat(zeilen("schritt")).isEqualTo(2);
        assertThat(zeilen("schritt_zutat")).isEqualTo(3);
    }

    @Test
    void laedtSchritteMitZutatenAuchAusserhalbDerTransaktion() {
        Long id = pfannkuchenAnlegen().getId();

        Rezept geladen = rezeptService.findeNachId(id).orElseThrow();

        assertThat(geladen.getZutaten()).extracting(Zutat::getName).containsExactlyInAnyOrder("Mehl", "Eier");
        assertThat(geladen.getSchritte()).extracting(Schritt::getBeschreibung)
            .containsExactly("Mehl und Eier verrühren", "Ausbacken");
        assertThat(geladen.getSchritte().getFirst().getZutaten())
            .extracting(verweis -> verweis.getZutat().getName())
            .containsExactlyInAnyOrder("Mehl", "Eier");
    }

    @Test
    void loeschenHinterlaesstKeineWaisen() {
        Long id = pfannkuchenAnlegen().getId();

        assertThat(rezeptService.loeschen(id)).isTrue();

        assertThat(zeilen("rezept")).isZero();
        assertThat(zeilen("zutat")).isZero();
        assertThat(zeilen("schritt")).isZero();
        assertThat(zeilen("schritt_zutat")).isZero();
    }

    @Test
    void entfernterSchrittNimmtSeineVerweiseMit() {
        Long id = pfannkuchenAnlegen().getId();

        transaktion.executeWithoutResult(status -> {
            Rezept rezept = repository.findById(id).orElseThrow();
            rezept.getSchritte().removeFirst();
        });

        assertThat(zeilen("schritt")).isEqualTo(1);
        assertThat(zeilen("schritt_zutat")).isEqualTo(1);
        assertThat(zeilen("zutat")).isEqualTo(2);
    }
}
