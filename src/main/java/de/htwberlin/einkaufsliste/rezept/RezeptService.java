package de.htwberlin.einkaufsliste.rezept;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RezeptService {

    private static final Logger log = LoggerFactory.getLogger(RezeptService.class);

    private final List<Rezept> rezepte = List.of(
        new Rezept(1L, "Spaghetti Carbonara", 2, "demo"),
        new Rezept(2L, "Linsensuppe", 4, "demo")
    );

    public List<Rezept> findAlle() {
        log.info("Gebe {} Rezepte zurueck", rezepte.size());
        return rezepte;
    }
}
