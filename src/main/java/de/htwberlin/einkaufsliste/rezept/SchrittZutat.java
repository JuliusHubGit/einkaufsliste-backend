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
 * Markiert nur, dass eine Zutat in einem Schritt vorkommt: keine eigene Menge (ADR 0001).
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class SchrittZutat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "schritt_id")
    private Schritt schritt;

    @ManyToOne
    @JoinColumn(name = "zutat_id")
    private Zutat zutat;

    public SchrittZutat(Schritt schritt, Zutat zutat) {
        this.schritt = schritt;
        this.zutat = zutat;
    }
}
