package de.htwberlin.einkaufsliste.rezept;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RezeptRepository extends JpaRepository<Rezept, Long> {
}
