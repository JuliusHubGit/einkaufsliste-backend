package de.htwberlin.einkaufsliste.rezept;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RezeptRepository extends JpaRepository<Rezept, Long> {

    // Zutaten gleich mitladen: das JSON wird erst nach Ende der Transaktion geschrieben
    @Override
    @EntityGraph(attributePaths = "zutaten")
    List<Rezept> findAll();

    @Override
    @EntityGraph(attributePaths = "zutaten")
    Optional<Rezept> findById(Long id);
}
