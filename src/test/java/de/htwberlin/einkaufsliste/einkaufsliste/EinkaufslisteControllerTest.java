package de.htwberlin.einkaufsliste.einkaufsliste;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
}
