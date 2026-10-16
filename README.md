# einkaufsliste-backend

Backend der **Rezept-Einkaufsliste** – Projekt im Modul Webtechnologien an der HTW Berlin.

## Thema

Die App verbindet eine Rezeptsammlung mit einer Einkaufsliste. Nutzer erfassen Rezepte
mit Titel, Portionenzahl, Zutaten (Name, Menge, Einheit) und geordneten
Arbeitsschritten. Per Knopfdruck lassen sich die Zutaten eines Rezepts auf die
Einkaufsliste übernehmen. Gleiche Zutaten werden dabei zu einem Eintrag
zusammengeführt. Die Einkaufsliste kann man auch unabhängig von Rezepten pflegen:
Einträge selbst anlegen, abhaken und löschen.

Geplante Use-Cases:

1. Rezepte anzeigen
2. Rezept anlegen
3. Rezept ansehen
4. Rezept löschen
5. Einkaufsliste anzeigen
6. Eintrag selbst anlegen
7. Zutaten eines Rezepts auf die Einkaufsliste übernehmen
8. Eintrag abhaken und löschen

Das zugehörige Frontend (Vue 3) liegt in
[einkaufsliste-frontend](https://github.com/JuliusHubGit/einkaufsliste-frontend).

## Technik

- Java 25, Spring Boot 4, Gradle
- REST-API unter `/api`
- Persistenz mit PostgreSQL ist geplant. Bis dahin kommen die Rezepte aus einer festen
  Liste im Service.

## Lokal starten

```bash
./gradlew bootRun
```

Danach liefert <http://localhost:8080/api/rezepte> die Liste aller Rezepte als JSON.

## Endpunkte

| Methode | Pfad | Beschreibung |
|---|---|---|
| `GET` | `/api/rezepte` | Alle Rezepte |
