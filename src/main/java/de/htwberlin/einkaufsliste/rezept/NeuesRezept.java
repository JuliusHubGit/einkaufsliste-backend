package de.htwberlin.einkaufsliste.rezept;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Eingabe beim Anlegen eines Rezepts.
 */
public record NeuesRezept(
    @NotBlank(message = "Bitte einen Titel eingeben")
    @Size(max = 150, message = "Der Titel darf höchstens 150 Zeichen lang sein")
    String titel,

    @Positive(message = "Die Portionen müssen mindestens 1 sein")
    int portionen,

    @NotNull(message = "Bitte eine Schwierigkeit wählen")
    Schwierigkeit schwierigkeit,

    List<@Valid NeueZutat> zutaten,

    List<@Valid NeuerSchritt> schritte
) {

    public record NeueZutat(
        @NotBlank(message = "Bitte einen Namen eingeben")
        String name,

        @Positive(message = "Die Menge muss größer als 0 sein")
        double menge,

        @NotBlank(message = "Bitte eine Einheit eingeben")
        @Size(max = 20, message = "Die Einheit darf höchstens 20 Zeichen lang sein")
        String einheit
    ) {
    }

    /**
     * @param zutaten Positionen (ab 0) in der Zutatenliste desselben Rezepts, da die Zutaten
     *                beim Anlegen noch keine Id haben
     */
    public record NeuerSchritt(
        @NotBlank(message = "Bitte den Schritt beschreiben")
        @Size(max = 2000, message = "Die Beschreibung darf höchstens 2000 Zeichen lang sein")
        String beschreibung,

        List<Integer> zutaten
    ) {
    }
}
