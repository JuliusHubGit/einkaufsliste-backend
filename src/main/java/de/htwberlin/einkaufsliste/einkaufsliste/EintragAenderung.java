package de.htwberlin.einkaufsliste.einkaufsliste;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Eingabe beim Bearbeiten oder Abhaken eines Einkaufslisteneintrags. Die Rezeptmenge bleibt
 * dabei unveraendert.
 */
public record EintragAenderung(
    @NotBlank(message = "Bitte einen Namen eingeben")
    @Size(max = 100, message = "Der Name darf höchstens 100 Zeichen lang sein")
    String name,

    @Positive(message = "Die Stückzahl muss mindestens 1 sein")
    int stueckzahl,

    boolean erledigt
) {
}
