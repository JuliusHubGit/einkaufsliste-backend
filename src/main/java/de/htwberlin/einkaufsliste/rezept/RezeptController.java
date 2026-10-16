package de.htwberlin.einkaufsliste.rezept;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> loeschen(@PathVariable Long id) {
        if (rezeptService.loeschen(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
