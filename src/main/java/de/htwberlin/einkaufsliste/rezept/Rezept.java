package de.htwberlin.einkaufsliste.rezept;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rezept {

    private Long id;
    private String titel;
    private int portionen;
    private String besitzer;
}
