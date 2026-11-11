package de.htwberlin.einkaufsliste.rezept;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.htwberlin.einkaufsliste.fehler.FeldfehlerException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RezeptService {

    private static final Logger log = LoggerFactory.getLogger(RezeptService.class);

    private final RezeptRepository rezeptRepository;

    public RezeptService(RezeptRepository rezeptRepository) {
        this.rezeptRepository = rezeptRepository;
    }

    public List<Rezept> findAlle() {
        List<Rezept> rezepte = rezeptRepository.findAll();
        log.info("Gebe {} Rezepte zurueck", rezepte.size());
        return rezepte;
    }

    public Optional<Rezept> findeNachId(Long id) {
        return rezeptRepository.findById(id);
    }

    @Transactional
    public Rezept anlegen(NeuesRezept neuesRezept) {
        Rezept rezept = new Rezept(null, neuesRezept.titel().trim(), neuesRezept.portionen(),
            neuesRezept.schwierigkeit(), "demo");
        List<NeuesRezept.NeueZutat> neueZutaten = neuesRezept.zutaten() == null ? List.of() : neuesRezept.zutaten();
        List<NeuesRezept.NeuerSchritt> neueSchritte = neuesRezept.schritte() == null ? List.of() : neuesRezept.schritte();
        pruefeZutatVerweise(neueSchritte, neueZutaten.size());

        for (NeuesRezept.NeueZutat zutat : neueZutaten) {
            rezept.zutatHinzufuegen(new Zutat(zutat.name().trim(), zutat.menge(), zutat.einheit().trim()));
        }
        for (int i = 0; i < neueSchritte.size(); i++) {
            NeuesRezept.NeuerSchritt neuerSchritt = neueSchritte.get(i);
            Schritt schritt = new Schritt(i + 1, neuerSchritt.beschreibung().trim());
            if (neuerSchritt.zutaten() != null) {
                // doppelte Verweise zaehlen nur einmal
                for (Integer index : new LinkedHashSet<>(neuerSchritt.zutaten())) {
                    schritt.zutatVerwenden(rezept.getZutaten().get(index));
                }
            }
            rezept.schrittHinzufuegen(schritt);
        }
        Rezept gespeichert = rezeptRepository.save(rezept);
        log.info("Rezept {} angelegt: {}", gespeichert.getId(), gespeichert.getTitel());
        return gespeichert;
    }

    private static void pruefeZutatVerweise(List<NeuesRezept.NeuerSchritt> schritte, int anzahlZutaten) {
        Map<String, String> feldfehler = new LinkedHashMap<>();
        for (int i = 0; i < schritte.size(); i++) {
            List<Integer> verweise = schritte.get(i).zutaten();
            if (verweise != null && verweise.stream().anyMatch(index -> index == null || index < 0 || index >= anzahlZutaten)) {
                feldfehler.put("schritte[" + i + "].zutaten", "Der Schritt verweist auf eine unbekannte Zutat");
            }
        }
        if (!feldfehler.isEmpty()) {
            log.warn("Rezept abgelehnt: {}", feldfehler.keySet());
            throw new FeldfehlerException(feldfehler);
        }
    }

    @Transactional
    public boolean loeschen(Long id) {
        if (!rezeptRepository.existsById(id)) {
            log.warn("Rezept {} zum Loeschen nicht gefunden", id);
            return false;
        }
        rezeptRepository.deleteById(id);
        log.info("Rezept {} geloescht", id);
        return true;
    }
}
