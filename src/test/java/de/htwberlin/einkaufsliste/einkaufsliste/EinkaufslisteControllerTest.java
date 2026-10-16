package de.htwberlin.einkaufsliste.einkaufsliste;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EinkaufslisteController.class)
class EinkaufslisteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EinkaufslisteService einkaufslisteService;

    @Test
    void gibAlleEintraegeAlsJsonZurueck() throws Exception {
        when(einkaufslisteService.findAlle())
            .thenReturn(List.of(new Einkaufslisteneintrag(1L, "Spaghetti", 1, "200 g", 1, false, "demo")));

        mockMvc.perform(get("/api/einkaufsliste"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(containsString("Spaghetti")))
            .andExpect(content().string(containsString("200 g")));
    }

    @Test
    void aendertReihenfolge() throws Exception {
        when(einkaufslisteService.reihenfolgeAendern(List.of(2L, 1L)))
            .thenReturn(List.of(
                new Einkaufslisteneintrag(2L, "Eier", 1, null, 1, false, "demo"),
                new Einkaufslisteneintrag(1L, "Spaghetti", 1, null, 2, false, "demo")));

        mockMvc.perform(put("/api/einkaufsliste/reihenfolge")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[2, 1]"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(containsString("Eier")));
    }

    @Test
    void lehntUngueltigeReihenfolgeAb() throws Exception {
        when(einkaufslisteService.reihenfolgeAendern(anyList()))
            .thenThrow(new IllegalArgumentException("ungueltig"));

        mockMvc.perform(put("/api/einkaufsliste/reihenfolge")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[99]"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void legtEintragMit201An() throws Exception {
        when(einkaufslisteService.anlegen(new NeuerEintrag("Butter", 2)))
            .thenReturn(new Einkaufslisteneintrag(5L, "Butter", 2, null, 5, false, "demo"));

        mockMvc.perform(post("/api/einkaufsliste")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Butter\", \"stueckzahl\": 2}"))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/api/einkaufsliste/5"))
            .andExpect(jsonPath("$.id").value(5))
            .andExpect(jsonPath("$.name").value("Butter"));
    }

    @Test
    void lehntLeerenNamenUndStueckzahlNullMitFeldfehlernAb() throws Exception {
        mockMvc.perform(post("/api/einkaufsliste")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"  \", \"stueckzahl\": 0}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.feldfehler.name").value("Bitte einen Namen eingeben"))
            .andExpect(jsonPath("$.feldfehler.stueckzahl").value("Die Stückzahl muss mindestens 1 sein"));

        verify(einkaufslisteService, never()).anlegen(any());
    }

    @Test
    void liefertNamensvorschlaege() throws Exception {
        when(einkaufslisteService.vorschlaege("mi")).thenReturn(List.of("Milch"));

        mockMvc.perform(get("/api/einkaufsliste/vorschlaege").param("q", "mi"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0]").value("Milch"));
    }
}
