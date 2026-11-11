package de.htwberlin.einkaufsliste.einkaufsliste;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EinkaufslisteneintragRepository extends JpaRepository<Einkaufslisteneintrag, Long> {

    List<Einkaufslisteneintrag> findAllByOrderByReihenfolgeAsc();
}
