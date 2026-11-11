package de.htwberlin.einkaufsliste.einkaufsliste;

import de.htwberlin.einkaufsliste.rezept.Zutat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EinkaufslisteService {

    private static final Logger log = LoggerFactory.getLogger(EinkaufslisteService.class);

    private final EinkaufslisteneintragRepository repository;

    public EinkaufslisteService(EinkaufslisteneintragRepository repository) {
        this.repository = repository;
    }

    public List<Einkaufslisteneintrag> findAlle() {
        List<Einkaufslisteneintrag> eintraege = repository.findAllByOrderByReihenfolgeAsc();
        log.info("Gebe {} Einkaufslisteneintraege zurueck", eintraege.size());
        return eintraege;
    }

    /**
     * Setzt die Reihenfolge neu. Die ID-Liste muss jeden vorhandenen Eintrag genau einmal enthalten.
     *
     * @throws IllegalArgumentException bei unbekannten, fehlenden oder doppelten IDs
     */
    @Transactional
    public List<Einkaufslisteneintrag> reihenfolgeAendern(List<Long> ids) {
        Map<Long, Einkaufslisteneintrag> nachId = repository.findAll().stream()
            .collect(Collectors.toMap(Einkaufslisteneintrag::getId, Function.identity()));
        if (ids == null || ids.size() != nachId.size() || !new HashSet<>(ids).equals(nachId.keySet())) {
            throw new IllegalArgumentException("Die IDs muessen jeden Eintrag genau einmal enthalten");
        }
        for (int i = 0; i < ids.size(); i++) {
            nachId.get(ids.get(i)).setReihenfolge(i + 1);
        }
        repository.saveAll(nachId.values());
        log.info("Reihenfolge der Einkaufsliste geaendert: {}", ids);
        return nachId.values().stream()
            .sorted(Comparator.comparingInt(Einkaufslisteneintrag::getReihenfolge))
            .toList();
    }

    @Transactional
    public Einkaufslisteneintrag anlegen(NeuerEintrag neuerEintrag) {
        int letztePosition = repository.findAll().stream()
            .mapToInt(Einkaufslisteneintrag::getReihenfolge)
            .max()
            .orElse(0);
        Einkaufslisteneintrag eintrag = repository.save(new Einkaufslisteneintrag(
            null,
            neuerEintrag.name().trim(),
            neuerEintrag.stueckzahl(),
            null,
            letztePosition + 1,
            false,
            "demo"));
        log.info("Einkaufslisteneintrag {} angelegt: {}", eintrag.getId(), eintrag.getName());
        return eintrag;
    }

    /**
     * Uebernimmt Zutaten auf die Einkaufsliste (ADR 0001): Gibt es schon einen Eintrag mit gleichem
     * normalisierten Namen, wird nur die Rezeptmenge angehaengt und der Eintrag wieder offen gesetzt,
     * die Stueckzahl bleibt. Sonst entsteht ein neuer Eintrag mit Stueckzahl 1 am Ende der Liste.
     *
     * @return die neu angelegten und ergaenzten Eintraege, jeder hoechstens einmal
     */
    @Transactional
    public List<Einkaufslisteneintrag> zutatenUebernehmen(List<Zutat> zutaten) {
        List<Einkaufslisteneintrag> vorhandene = new ArrayList<>(repository.findAllByOrderByReihenfolgeAsc());
        int letztePosition = vorhandene.stream().mapToInt(Einkaufslisteneintrag::getReihenfolge).max().orElse(0);
        Map<Long, Einkaufslisteneintrag> betroffen = new LinkedHashMap<>();

        for (Zutat zutat : zutaten) {
            String normalform = Namen.normalisieren(zutat.getName());
            String menge = mengeAlsText(zutat);
            Optional<Einkaufslisteneintrag> passend = vorhandene.stream()
                .filter(eintrag -> Namen.normalisieren(eintrag.getName()).equals(normalform))
                .findFirst();
            Einkaufslisteneintrag eintrag;
            if (passend.isPresent()) {
                eintrag = passend.get();
                eintrag.setRezeptmenge(eintrag.getRezeptmenge() == null || eintrag.getRezeptmenge().isBlank()
                    ? menge
                    : eintrag.getRezeptmenge() + ", " + menge);
                eintrag.setErledigt(false);
                eintrag = repository.save(eintrag);
            } else {
                eintrag = repository.save(new Einkaufslisteneintrag(
                    null, zutat.getName().trim(), 1, menge, ++letztePosition, false, "demo"));
                vorhandene.add(eintrag);
            }
            betroffen.put(eintrag.getId(), eintrag);
        }
        log.info("{} Zutaten uebernommen, {} Einkaufslisteneintraege betroffen", zutaten.size(), betroffen.size());
        return List.copyOf(betroffen.values());
    }

    private static String mengeAlsText(Zutat zutat) {
        DecimalFormat format = new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.GERMAN));
        return format.format(zutat.getMenge()) + " " + zutat.getEinheit();
    }

    @Transactional
    public Optional<Einkaufslisteneintrag> aendern(Long id, EintragAenderung aenderung) {
        Optional<Einkaufslisteneintrag> gefunden = repository.findById(id).map(eintrag -> {
            eintrag.setName(aenderung.name().trim());
            eintrag.setStueckzahl(aenderung.stueckzahl());
            eintrag.setErledigt(aenderung.erledigt());
            log.info("Einkaufslisteneintrag {} geaendert", id);
            return repository.save(eintrag);
        });
        if (gefunden.isEmpty()) {
            log.warn("Einkaufslisteneintrag {} zum Aendern nicht gefunden", id);
        }
        return gefunden;
    }

    @Transactional
    public boolean loeschen(Long id) {
        if (!repository.existsById(id)) {
            log.warn("Einkaufslisteneintrag {} zum Loeschen nicht gefunden", id);
            return false;
        }
        repository.deleteById(id);
        log.info("Einkaufslisteneintrag {} geloescht", id);
        return true;
    }

    /**
     * Namensvorschlaege aus den vorhandenen Eintraegen: ohne Duplikate (Gross-/Kleinschreibung
     * egal), alphabetisch, hoechstens 10.
     */
    public List<String> vorschlaege(String suchtext) {
        String suche = Namen.normalisieren(suchtext);
        Map<String, String> nachNormalform = new LinkedHashMap<>();
        for (Einkaufslisteneintrag eintrag : repository.findAll()) {
            String normalform = Namen.normalisieren(eintrag.getName());
            if (normalform.contains(suche)) {
                nachNormalform.putIfAbsent(normalform, eintrag.getName());
            }
        }
        return nachNormalform.values().stream()
            .sorted(String.CASE_INSENSITIVE_ORDER)
            .limit(10)
            .toList();
    }

}
