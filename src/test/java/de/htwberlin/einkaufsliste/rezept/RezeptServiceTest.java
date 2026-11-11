package de.htwberlin.einkaufsliste.rezept;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
        doReturn(List.of(new Rezept(1L, "Spaghetti Carbonara", 2, "demo"),
            new Rezept(2L, "Linsensuppe", 4, "demo"))).when(repository).findAll();

        assertThat(rezeptService.findAlle()).extracting(Rezept::getTitel)
            .containsExactly("Spaghetti Carbonara", "Linsensuppe");
    }

    @Test
    void findeNachIdLiefertVorhandenesRezept() {
        doReturn(Optional.of(new Rezept(1L, "Spaghetti Carbonara", 2, "demo"))).when(repository).findById(1L);

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
