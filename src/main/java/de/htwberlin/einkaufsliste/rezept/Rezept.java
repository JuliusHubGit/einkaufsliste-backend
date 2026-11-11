package de.htwberlin.einkaufsliste.rezept;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    // orphanRemoval: sonst bleiben beim Loeschen/Bearbeiten Zutaten-Waisen in der Datenbank
    @OneToMany(mappedBy = "rezept", cascade = CascadeType.ALL, orphanRemoval = true)
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
}
