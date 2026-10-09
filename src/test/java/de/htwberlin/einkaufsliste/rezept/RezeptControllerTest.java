package de.htwberlin.einkaufsliste.rezept;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RezeptController.class)
class RezeptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RezeptService rezeptService;

    @Test
    void gibAlleRezepteAlsJsonZurueck() throws Exception {
        when(rezeptService.findAlle()).thenReturn(List.of(new Rezept(1L, "Spaghetti Carbonara", 2, "demo")));

        mockMvc.perform(get("/api/rezepte"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(containsString("Spaghetti Carbonara")));
    }
}
