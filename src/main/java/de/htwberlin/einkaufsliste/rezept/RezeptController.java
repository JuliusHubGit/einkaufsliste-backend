package de.htwberlin.einkaufsliste.rezept;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rezepte")
public class RezeptController {

    private final RezeptService rezeptService;

    public RezeptController(RezeptService rezeptService) {
        this.rezeptService = rezeptService;
    }

    @GetMapping
    public ResponseEntity<List<Rezept>> alleRezepte() {
        return ResponseEntity.ok(rezeptService.findAlle());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rezept> rezeptNachId(@PathVariable Long id) {
        return rezeptService.findeNachId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
