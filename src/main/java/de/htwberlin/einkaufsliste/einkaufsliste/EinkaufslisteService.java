package de.htwberlin.einkaufsliste.einkaufsliste;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EinkaufslisteService {

    private static final Logger log = LoggerFactory.getLogger(EinkaufslisteService.class);

    private final List<Einkaufslisteneintrag> eintraege = new ArrayList<>(List.of(
        new Einkaufslisteneintrag(1L, "Spaghetti", 1, "200 g", 1, false, "demo"),
        new Einkaufslisteneintrag(2L, "Eier", 1, null, 2, false, "demo"),
        new Einkaufslisteneintrag(3L, "Milch", 2, null, 3, false, "demo"),
        new Einkaufslisteneintrag(4L, "Linsen", 1, "250 g", 4, true, "demo")
    ));

    private final AtomicLong naechsteId = new AtomicLong(5);

    public synchronized List<Einkaufslisteneintrag> findAlle() {
        log.info("Gebe {} Einkaufslisteneintraege zurueck", eintraege.size());
        return eintraege.stream()
            .sorted(Comparator.comparingInt(Einkaufslisteneintrag::getReihenfolge))
            .toList();
    }

    /**
     * Setzt die Reihenfolge neu. Die ID-Liste muss jeden vorhandenen Eintrag genau einmal enthalten.
     *
     * @throws IllegalArgumentException bei unbekannten, fehlenden oder doppelten IDs
     */
    public synchronized List<Einkaufslisteneintrag> reihenfolgeAendern(List<Long> ids) {
        Map<Long, Einkaufslisteneintrag> nachId = eintraege.stream()
            .collect(Collectors.toMap(Einkaufslisteneintrag::getId, Function.identity()));
        if (ids == null || ids.size() != nachId.size() || !new HashSet<>(ids).equals(nachId.keySet())) {
            throw new IllegalArgumentException("Die IDs muessen jeden Eintrag genau einmal enthalten");
        }
        for (int i = 0; i < ids.size(); i++) {
            nachId.get(ids.get(i)).setReihenfolge(i + 1);
        }
        log.info("Reihenfolge der Einkaufsliste geaendert: {}", ids);
        return findAlle();
    }

    public synchronized Einkaufslisteneintrag anlegen(NeuerEintrag neuerEintrag) {
        int letztePosition = eintraege.stream()
            .mapToInt(Einkaufslisteneintrag::getReihenfolge)
            .max()
            .orElse(0);
        Einkaufslisteneintrag eintrag = new Einkaufslisteneintrag(
            naechsteId.getAndIncrement(),
            neuerEintrag.name().trim(),
            neuerEintrag.stueckzahl(),
            null,
            letztePosition + 1,
            false,
            "demo");
        eintraege.add(eintrag);
        log.info("Einkaufslisteneintrag {} angelegt: {}", eintrag.getId(), eintrag.getName());
        return eintrag;
    }

    /**
     * Namensvorschlaege aus den vorhandenen Eintraegen: ohne Duplikate (Gross-/Kleinschreibung
     * egal), alphabetisch, hoechstens 10.
     */
    public synchronized List<String> vorschlaege(String suchtext) {
        String suche = normalisieren(suchtext);
        Map<String, String> nachNormalform = new LinkedHashMap<>();
        for (Einkaufslisteneintrag eintrag : eintraege) {
            String normalform = normalisieren(eintrag.getName());
            if (normalform.contains(suche)) {
                nachNormalform.putIfAbsent(normalform, eintrag.getName());
            }
        }
        return nachNormalform.values().stream()
            .sorted(String.CASE_INSENSITIVE_ORDER)
            .limit(10)
            .toList();
    }

    private static String normalisieren(String text) {
        return text == null ? "" : text.trim().toLowerCase(Locale.GERMAN);
    }
}
