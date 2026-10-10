package de.htwberlin.einkaufsliste.config;

import de.htwberlin.einkaufsliste.rezept.RezeptController;
import de.htwberlin.einkaufsliste.rezept.RezeptService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@WebMvcTest(RezeptController.class)
class WebConfigCorsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RezeptService rezeptService;

    @Test
    void erlaubtKonfigurierteOrigin() throws Exception {
        when(rezeptService.findAlle()).thenReturn(List.of());

        mockMvc.perform(get("/api/rezepte").header("Origin", "http://localhost:5173"))
            .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    @Test
    void verweigertUnbekannteOrigin() throws Exception {
        when(rezeptService.findAlle()).thenReturn(List.of());

        mockMvc.perform(get("/api/rezepte").header("Origin", "https://boesartig.example.com"))
            .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }
}
