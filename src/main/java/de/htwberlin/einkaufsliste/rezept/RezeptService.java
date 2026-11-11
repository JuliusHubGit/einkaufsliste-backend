package de.htwberlin.einkaufsliste.rezept;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        if (neuesRezept.zutaten() != null) {
            for (NeuesRezept.NeueZutat zutat : neuesRezept.zutaten()) {
                rezept.zutatHinzufuegen(new Zutat(zutat.name().trim(), zutat.menge(), zutat.einheit().trim()));
            }
        }
        Rezept gespeichert = rezeptRepository.save(rezept);
        log.info("Rezept {} angelegt: {}", gespeichert.getId(), gespeichert.getTitel());
        return gespeichert;
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
