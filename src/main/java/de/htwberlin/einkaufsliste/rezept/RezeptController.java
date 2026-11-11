package de.htwberlin.einkaufsliste.rezept;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
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

    @PostMapping
    public ResponseEntity<Rezept> anlegen(@Valid @RequestBody NeuesRezept neuesRezept) {
        Rezept rezept = rezeptService.anlegen(neuesRezept);
        return ResponseEntity.created(URI.create("/api/rezepte/" + rezept.getId())).body(rezept);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> loeschen(@PathVariable Long id) {
        if (rezeptService.loeschen(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
