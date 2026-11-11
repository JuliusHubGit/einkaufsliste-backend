package de.htwberlin.einkaufsliste.rezept;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Posten auf der Zutatenliste eines Rezepts. Existiert nur als Kind eines Rezepts.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Zutat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double menge;
    private String einheit;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "rezept_id")
    private Rezept rezept;

    public Zutat(String name, double menge, String einheit) {
        this.name = name;
        this.menge = menge;
        this.einheit = einheit;
    }
}
