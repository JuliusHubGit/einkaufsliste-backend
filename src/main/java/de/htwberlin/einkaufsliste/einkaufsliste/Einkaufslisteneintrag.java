package de.htwberlin.einkaufsliste.einkaufsliste;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Einkaufslisteneintrag {

    private Long id;
    private String name;
    private int stueckzahl;
    private String rezeptmenge;
    private int reihenfolge;
    private boolean erledigt;
    private String besitzer;
}
