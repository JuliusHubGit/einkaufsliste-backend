package de.htwberlin.einkaufsliste.rezept;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Ein geordneter Arbeitsschritt. Verweist auf Zutaten des Rezepts, ueberschreibt deren Menge nicht.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Schritt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int position;

    @Column(length = 2000)
    private String beschreibung;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "rezept_id")
    private Rezept rezept;

    @OneToMany(mappedBy = "schritt", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private List<SchrittZutat> zutaten = new ArrayList<>();

    public Schritt(int position, String beschreibung) {
        this.position = position;
        this.beschreibung = beschreibung;
    }

    public void zutatVerwenden(Zutat zutat) {
        zutaten.add(new SchrittZutat(this, zutat));
    }
}
