package de.htwberlin.einkaufsliste.einkaufsliste;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/einkaufsliste")
public class EinkaufslisteController {

    private final EinkaufslisteService einkaufslisteService;

    public EinkaufslisteController(EinkaufslisteService einkaufslisteService) {
        this.einkaufslisteService = einkaufslisteService;
    }

    @GetMapping
    public ResponseEntity<List<Einkaufslisteneintrag>> alleEintraege() {
        return ResponseEntity.ok(einkaufslisteService.findAlle());
    }

    @PostMapping
    public ResponseEntity<Einkaufslisteneintrag> anlegen(@Valid @RequestBody NeuerEintrag neuerEintrag) {
        Einkaufslisteneintrag eintrag = einkaufslisteService.anlegen(neuerEintrag);
        return ResponseEntity.created(URI.create("/api/einkaufsliste/" + eintrag.getId())).body(eintrag);
    }

    @GetMapping("/vorschlaege")
    public ResponseEntity<List<String>> vorschlaege(@RequestParam(defaultValue = "") String q) {
        return ResponseEntity.ok(einkaufslisteService.vorschlaege(q));
    }

    @PutMapping("/reihenfolge")
    public ResponseEntity<List<Einkaufslisteneintrag>> reihenfolgeAendern(@RequestBody List<Long> ids) {
        try {
            return ResponseEntity.ok(einkaufslisteService.reihenfolgeAendern(ids));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
