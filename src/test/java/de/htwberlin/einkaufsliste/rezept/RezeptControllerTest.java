package de.htwberlin.einkaufsliste.rezept;

import de.htwberlin.einkaufsliste.fehler.FeldfehlerException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RezeptController.class)
class RezeptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RezeptService rezeptService;

    @Test
    void gibAlleRezepteAlsJsonZurueck() throws Exception {
        when(rezeptService.findAlle()).thenReturn(List.of(new Rezept(1L, "Spaghetti Carbonara", 2, Schwierigkeit.NORMAL, "demo")));

        mockMvc.perform(get("/api/rezepte"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(containsString("Spaghetti Carbonara")));
    }

    @Test
    void gibEinzelnesRezeptZurueck() throws Exception {
        when(rezeptService.findeNachId(1L)).thenReturn(Optional.of(new Rezept(1L, "Spaghetti Carbonara", 2, Schwierigkeit.NORMAL, "demo")));

        mockMvc.perform(get("/api/rezepte/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(containsString("Spaghetti Carbonara")));
    }

    @Test
    void gibNotFoundBeiUnbekannterId() throws Exception {
        when(rezeptService.findeNachId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/rezepte/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void loeschtRezeptMit204() throws Exception {
        when(rezeptService.loeschen(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/rezepte/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void gibt404BeiUnbekanntemRezept() throws Exception {
        when(rezeptService.loeschen(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/rezepte/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void legtRezeptMit201An() throws Exception {
        Rezept rezept = new Rezept(3L, "Pfannkuchen", 2, Schwierigkeit.EINFACH, "demo");
        rezept.zutatHinzufuegen(new Zutat("Mehl", 200, "g"));
        when(rezeptService.anlegen(any())).thenReturn(rezept);

        mockMvc.perform(post("/api/rezepte")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"titel": "Pfannkuchen", "portionen": 2, "schwierigkeit": "EINFACH",
                     "zutaten": [{"name": "Mehl", "menge": 200, "einheit": "g"}]}
                    """))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/api/rezepte/3"))
            .andExpect(jsonPath("$.schwierigkeit").value("EINFACH"))
            .andExpect(jsonPath("$.zutaten[0].name").value("Mehl"));
    }

    @Test
    void lehntRezeptOhneTitelAb() throws Exception {
        mockMvc.perform(post("/api/rezepte")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"titel": " ", "portionen": 2, "schwierigkeit": "NORMAL", "zutaten": []}
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.feldfehler.titel").value("Bitte einen Titel eingeben"));
        verify(rezeptService, never()).anlegen(any());
    }

    @Test
    void lehntRezeptOhneSchwierigkeitAb() throws Exception {
        mockMvc.perform(post("/api/rezepte")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"titel": "Pfannkuchen", "portionen": 2, "zutaten": []}
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.feldfehler.schwierigkeit").value("Bitte eine Schwierigkeit wählen"));
    }

    @Test
    void lehntUnbekannteSchwierigkeitAb() throws Exception {
        mockMvc.perform(post("/api/rezepte")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"titel": "Pfannkuchen", "portionen": 2, "schwierigkeit": "SCHWER", "zutaten": []}
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.feldfehler.schwierigkeit").value("Ungültiger Wert"));
    }

    @Test
    void lehntUngueltigeZutatAb() throws Exception {
        mockMvc.perform(post("/api/rezepte")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"titel": "Pfannkuchen", "portionen": 2, "schwierigkeit": "NORMAL",
                     "zutaten": [{"name": "Mehl", "menge": 0, "einheit": "g"}]}
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.feldfehler['zutaten[0].menge']").value("Die Menge muss größer als 0 sein"));
    }

    @Test
    void gibtSchritteMitIhrenZutatenZurueck() throws Exception {
        Rezept rezept = new Rezept(3L, "Pfannkuchen", 2, Schwierigkeit.EINFACH, "demo");
        Zutat mehl = new Zutat("Mehl", 200, "g");
        rezept.zutatHinzufuegen(mehl);
        Schritt schritt = new Schritt(1, "Mehl sieben");
        schritt.zutatVerwenden(mehl);
        rezept.schrittHinzufuegen(schritt);
        when(rezeptService.findeNachId(3L)).thenReturn(Optional.of(rezept));

        mockMvc.perform(get("/api/rezepte/3"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.schritte[0].position").value(1))
            .andExpect(jsonPath("$.schritte[0].beschreibung").value("Mehl sieben"))
            .andExpect(jsonPath("$.schritte[0].zutaten[0].zutat.name").value("Mehl"));
    }

    @Test
    void lehntSchrittOhneBeschreibungAb() throws Exception {
        mockMvc.perform(post("/api/rezepte")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"titel": "Pfannkuchen", "portionen": 2, "schwierigkeit": "NORMAL",
                     "zutaten": [], "schritte": [{"beschreibung": "", "zutaten": []}]}
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.feldfehler['schritte[0].beschreibung']").value("Bitte den Schritt beschreiben"));
    }

    @Test
    void meldetFachlicheFeldfehlerDesServiceMit400() throws Exception {
        when(rezeptService.anlegen(any()))
            .thenThrow(new FeldfehlerException(Map.of("schritte[0].zutaten", "Der Schritt verweist auf eine unbekannte Zutat")));

        mockMvc.perform(post("/api/rezepte")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"titel": "Pfannkuchen", "portionen": 2, "schwierigkeit": "NORMAL",
                     "zutaten": [], "schritte": [{"beschreibung": "Verrühren", "zutaten": [5]}]}
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.feldfehler['schritte[0].zutaten']").value("Der Schritt verweist auf eine unbekannte Zutat"));
    }
}
