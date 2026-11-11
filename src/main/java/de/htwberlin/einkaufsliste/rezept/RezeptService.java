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
