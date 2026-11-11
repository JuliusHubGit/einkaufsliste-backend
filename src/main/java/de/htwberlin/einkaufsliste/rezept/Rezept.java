package de.htwberlin.einkaufsliste.rezept;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Rezept {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titel;
    private int portionen;

    @Enumerated(EnumType.STRING)
    private Schwierigkeit schwierigkeit;

    // orphanRemoval: sonst bleiben beim Loeschen/Bearbeiten Waisen in der Datenbank.
    // Eager + SUBSELECT: das JSON wird erst nach Ende der Transaktion geschrieben, und zwei
    // Listen lassen sich nicht in einem Join laden.
    // Schritte stehen vor den Zutaten, damit ihre Verweise auf Zutaten zuerst geloescht werden.
    @OneToMany(mappedBy = "rezept", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @OrderBy("position")
    private List<Schritt> schritte = new ArrayList<>();

    @OneToMany(mappedBy = "rezept", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private List<Zutat> zutaten = new ArrayList<>();

    private String besitzer;

    public Rezept(Long id, String titel, int portionen, Schwierigkeit schwierigkeit, String besitzer) {
        this.id = id;
        this.titel = titel;
        this.portionen = portionen;
        this.schwierigkeit = schwierigkeit;
        this.besitzer = besitzer;
    }

    public void zutatHinzufuegen(Zutat zutat) {
        zutat.setRezept(this);
        zutaten.add(zutat);
    }

    public void schrittHinzufuegen(Schritt schritt) {
        schritt.setRezept(this);
        schritte.add(schritt);
    }
}
