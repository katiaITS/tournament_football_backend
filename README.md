# Tournament Football Backend

Sistema backend per la gestione di tornei di calcetto sviluppato con Spring Boot, Spring Security e MySQL.

## Indice

- [Panoramica](#panoramica)
- [Tecnologie Utilizzate](#tecnologie-utilizzate)
- [Architettura](#architettura)
- [Requisiti](#requisiti)
- [Installazione e Avvio](#installazione-e-avvio)
- [API Endpoints](#api-endpoints)
- [Testing](#testing)
- [Dati di Test](#dati-di-test)

## Panoramica

Tournament Football Backend è un'applicazione per digitalizzare la gestione di competizioni sportive, sostituendo la gestione cartacea tradizionale. Il sistema è pensato:
- **Per organizzazioni sportive locali**: Gestione semplice di tornei tra amici o squadre amatoriali
- **Per centri sportivi**: Soluzione completa per gestire più tornei contemporaneamente

### Obiettivi del Progetto

- API REST complete per operazioni CRUD su utenti, squadre, tornei e partite
- Database MySQL con implementazione di tutte le relazioni JPA (OneToOne, OneToMany, ManyToOne, ManyToMany)
- Sistema di autenticazione sicuro con JWT e ruoli USER/ADMIN
- Test automatici con copertura superiore al 35%
- Containerizzazione Docker per facilità di deployment
- Documentazione completa delle API con Postman

## Tecnologie Utilizzate

### Backend Framework
- **Spring Boot 3.5.4**
- **Spring Security** - Autenticazione JWT e autorizzazione basata su ruoli
- **Spring Data JPA** - Persistenza dati e ORM
- **JWT** - Token di autenticazione

### Database e Persistenza
- **MySQL 8.0** - Database relazionale principale
- **H2** - Database per test
- **phpMyAdmin** - Gestione database via web

### Build e Deployment
- **Maven 3.9+** - Gestione dipendenze e build
- **Docker 20.10+** - Containerizzazione
- **Docker Compose 2.0+** - Orchestrazione multi-container
- **Java 21**

### Testing
- **JUnit 5 + Mockito** - Test automatici
- **Postman** - Test API

## Architettura

```
┌─────────────────┐
│   Controllers   │ ← Gestiscono le richieste HTTP
├─────────────────┤
│    Services     │ ← Contengono la logica di business
├─────────────────┤
│  Repositories   │ ← Si occupano del database
├─────────────────┤
│     Models      │ ← Rappresentano le tabelle del database
└─────────────────┘
```
## Requisiti

- **Docker 20.10+**
- **Docker Compose 2.0+**

## Installazione e Avvio

### 1. Clonazione del Repository
```bash
git clone <repository-url>
cd tournament-football-backend
```

### 2. Avvio dei Servizi
```bash
docker compose up --build -d
```

### 3. Accesso all'Applicazione
- **API**: http://localhost:8080/api
- **phpMyAdmin**: http://localhost:8081
- **Database**: localhost:3306

### 4. Verifica Stato Container (Opzionale)
```bash
docker compose ps
```

### 5. Visualizzazione Log (Opzionale)
```bash
docker compose logs -f
```

### 6. Arresto Servizi
```bash
docker compose down
```

## API Endpoints

### Autenticazione (`/auth`)
- `POST /auth/login` - Login utente
- `POST /auth/register` - Registrazione

### Gestione Utenti (`/users`)
- `GET /users` - Lista utenti (ADMIN)
- `GET /users/search?keyword={keyword}` - Ricerca utenti (ADMIN)
- `GET /users/username/{username}` - Dettagli utente per username (ADMIN o proprietario)
- `GET /users/{id}` - Dettagli utente (ADMIN o proprietario)
- `PUT /users/{id}/profile` - Aggiorna profilo (ADMIN o proprietario)
- `PUT /users/{id}` - Aggiorna utente (ADMIN o proprietario)
- `DELETE /users/{id}` - Elimina utente (ADMIN)

### Gestione Squadre (`/teams`)
- `GET /teams` - Lista squadre
- `GET /teams/{id}` - Dettagli squadra
- `GET /teams/name/{name}` - Dettagli squadra per nome
- `POST /teams` - Crea squadra
- `PUT /teams/{id}` - Aggiorna squadra (ADMIN)
- `DELETE /teams/{id}` - Elimina squadra (ADMIN)
- `POST /teams/{teamId}/players/{playerId}` - Aggiungi giocatore (ADMIN)
- `DELETE /teams/{teamId}/players/{playerId}` - Rimuovi giocatore (ADMIN)
- `GET /teams/player/{playerId}` - Squadre per giocatore
- `GET /teams/search?keyword={keyword}` - Ricerca squadre

### Gestione Tornei (`/tournaments`)
- `GET /tournaments` - Lista tornei
- `GET /tournaments/{id}` - Dettagli torneo
- `POST /tournaments` - Crea torneo (ADMIN)
- `PUT /tournaments/{id}` - Aggiorna torneo (ADMIN)
- `DELETE /tournaments/{id}` - Elimina torneo (ADMIN)
- `POST /tournaments/{tournamentId}/teams/{teamId}` - Iscrivi squadra
- `DELETE /api/tournaments/{tournamentId}/teams/{teamId}` - Rimuovi squadra da torneo (ADMIN)
- `GET /tournaments/status/{status}` - Filtra per stato
- `GET /tournaments/upcoming` - Tornei futuri
- `GET /api/tournaments/team/{teamId}` - Tornei per squadra
- `GET /tournaments/search?keyword={keyword}` - Ricerca tornei

### Gestione Partite (`/matches`)
- `GET /matches` - Lista partite
- `GET /matches/{id}` - Dettagli partita per id
- `POST /matches` - Crea partita (ADMIN)
- `PUT /matches/{id}` - Aggiorna match (ADMIN)
- `PUT /matches/{id}/result` - Aggiorna risultato (ADMIN)
- `DELETE /matches/{id}` - Elimina partita (ADMIN)
- `GET /matches/tournament/{id}` - Partite per torneo
- `GET /matches/team/{id}` - Partite per squadra
- `GET /matches/status/{status}` - Partite per stato
- `GET /matches/period?start={start}&end={end}` - Partite per periodo
- `GET /matches/today` - Partite odierne


### Codici di Stato HTTP

| Codice | Significato | Utilizzo |
|--------|-------------|----------|
| 200 | OK | Operazioni riuscite |
| 201 | Created | Creazione risorsa |
| 400 | Bad Request | Dati non validi |
| 401 | Unauthorized | Token mancante/invalido |
| 403 | Forbidden | Permessi insufficienti |
| 404 | Not Found | Risorsa inesistente |
| 409 | Conflict | Conflitto logico |

## Testing

### Test Automatici
Copertura superiore al 35% con JUnit 5 + Mockito:

| Test Suite | Focus |
|------------|-------|
| MatchServiceTest | Logica partite, controllo punteggi |
| TeamServiceTest | Gestione giocatori, ricerche |
| TournamentServiceTest | Stati tornei, logica complessa |
| UserServiceTest | Autenticazione, profili |
| UserDetailsServiceImplTest | Integrazione Spring Security |

### Esecuzione Test
Per eseguire i test automatici è consigliato utilizzare IntelliJ IDEA con plugin Maven.

#### Esecuzione con IntelliJ IDEA da terminale
1. Aprire il progetto in IntelliJ IDEA
2. Aprire il terminale integrato
3. Eseguire il comando:
   ```bash
   ./mvnw clean test
   ```
#### Esecuzione con IntelliJ IDEA GUI
IntelliJ IDEA permette di eseguire i test direttamente dall'interfaccia grafica:
1. Aprire il progetto in IntelliJ IDEA
2. Navigare su `Run` → `Run All Tests with Coverage`

### Test Manuali
Per testare manualmente le API, è possibile utilizzare Postman è presente una collection completa con tutti gli endpoint e i test di business logic.

#### Test API con Postman
Collection completa disponibile in `Tournament Football API.postman_collection.json`:

**Funzionalità:**
- Gestione automatica JWT
- Variabili dinamiche
- Workflow completo di test (12 step)
- Test regole di business (Presentano errori relative al tipo di test)

**Utilizzo:**
1. Importare collection in Postman
2. Eseguire "Complete Testing Workflow" per testare i 12 step principali
3. Oppure eseguire i singoli endpoint per testare funzionalità specifiche

*NOTA: è possibile che alcuni test presentino errori, in quanto sono stati creati per verificare la risposta del sistema a condizioni specifiche (es. errori di validazione, permessi insufficienti).*

## Dati di Test

### Account Preconfigurati
- **Admin**: `admin` / `password123`
- **Admin Secondario**: `matteo_bronze` / `password123`
- **Utenti Standard**: `mario_rossi`, `luca_bianchi`, `giuseppe_verdi` / `password123`

*Le password nei dati di test sono già hashate con BCrypt, ma per comodità la password in chiaro è `password123` per tutti gli utenti di test.*

### Dati Precaricati
- **15 utenti** con profili completi
- **12 squadre** con giocatori assegnati
- **7 tornei** in diversi stati
- **25+ partite** con risultati e calendario
- **Relazioni complete** tra tutte le entità

### Schema Database
- 7 tabelle principali (users, profiles, teams, tournaments, matches)
- 2 tabelle di relazione (team_players, tournament_teams)
- Vincoli di integrità referenziale
- Indici per performance ottimizzate