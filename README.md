# Tournament Football Backend

Sistema backend per la gestione di tornei di calcetto sviluppato con Spring Boot, Spring Security e MySQL.

## Indice

- [Panoramica](#panoramica)
- [Tecnologie Utilizzate](#tecnologie-utilizzate)
- [Architettura](#architettura)
- [Requisiti](#requisiti)
- [Installazione e Avvio](#installazione-e-avvio)
- [API Endpoints](#api-endpoints)
- [Configurazione](#configurazione)
- [Testing](#testing)
- [Dati di Test](#dati-di-test)
- [Sviluppi Futuri](#sviluppi-futuri)

## Panoramica

Tournament Football Backend è un'applicazione per digitalizzare la gestione di competizioni sportive, sostituendo la gestione cartacea tradizionale. Il sistema offre:

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

### Moduli Principali

#### Modulo User (Utenti)
- **Controller**: `AuthController`, `UserController`
- **Service**: `UserService`, `UserDetailsServiceImpl`
- **Repository**: `UserRepository`, `ProfileRepository`
- **Entità**: `User`, `Profile`
- **Relazioni JPA**: OneToOne (User ↔ Profile), ManyToMany (User ↔ Team)
- **Funzionalità**: Autenticazione JWT, gestione profili, autorizzazioni

#### Modulo Team (Squadre)
- **Controller**: `TeamController`
- **Service**: `TeamService`
- **Repository**: `TeamRepository`
- **Entità**: `Team`
- **Relazioni JPA**: ManyToMany (Team ↔ Users e Tournament ↔ Teams)
- **Funzionalità**: Gestione squadre e giocatori

#### Modulo Tournament (Tornei)
- **Controller**: `TournamentController`
- **Service**: `TournamentService`
- **Repository**: `TournamentRepository`
- **Entità**: `Tournament`
- **Relazioni JPA**: OneToMany (Tournament → Match), ManyToMany (Tournament ↔ Teams)
- **Stati**: OPEN, IN_PROGRESS, COMPLETED, CANCELLED, SCHEDULED
- **Funzionalità**: Gestione tornei e iscrizioni

#### Modulo Match (Partite)
- **Controller**: `MatchController`
- **Service**: `MatchService`
- **Repository**: `MatchRepository`
- **Entità**: `Match`
- **Relazioni JPA**: ManyToOne (Match → Teams, Match → Tournament)
- **Stati**: SCHEDULED, IN_PROGRESS, COMPLETED, POSTPONED, CANCELLED, TO_BE_SCHEDULED
- **Funzionalità**: Programmazione partite e risultati

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
docker-compose up --build -d
```

### 3. Accesso all'Applicazione
- **API**: http://localhost:8080/api
- **phpMyAdmin**: http://localhost:8081
- **Database**: http://localhost:3306

### 4. Verifica Stato Container (Opzionale)
```bash
docker-compose ps
```

### 5. Visualizzazione Log (Opzionale)
```bash
docker-compose logs -f
```

### 6. Arresto Servizi
```bash
docker-compose down
```

## API Endpoints

### Autenticazione (`/auth`)

#### POST `/auth/login` - Login utente
- **Request Body**:
    ```json
    {
    "username": "admin",
    "password": "password123"
    }
    ```
- **Response**
    - **Success (200 OK)**:
        ```json
        {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "type": "Bearer",
        "username": "admin",
        "email": null,
        "role": "ROLE_ADMIN"
        }
        ```
    - **Error (401 Unauthorized)**: `{ "error": "Invalid credentials" }`

#### POST `/auth/register` - Registrazione
- **Request Body**:
    ```json
    {
    "username": "nuovo_utente",
    "email": "utente@email.com",
    "password": "password123"
    }
    ```
- **Response**
    - **Success (201 Created)**:
        ```json
        {
        "message": "User registered successfully!"
        }
        ```
    - **Error (400 Bad Request)**: `{ "error": "Username already exists" }`

### Gestione Utenti (`/users`)

- `GET /users` - Lista utenti (ADMIN)
- `GET /users/{id}` - Dettagli utente (ADMIN o proprietario)
- `PUT /users/{id}/profile` - Aggiorna profilo (ADMIN o proprietario)
- `GET /users/search?keyword={keyword}` - Ricerca utenti (ADMIN)

### Gestione Squadre (`/teams`)

- `GET /teams` - Lista squadre
- `POST /teams` - Crea squadra
- `GET /teams/{id}` - Dettagli squadra
- `POST /teams/{teamId}/players/{playerId}` - Aggiungi giocatore (ADMIN)
- `DELETE /teams/{teamId}/players/{playerId}` - Rimuovi giocatore (ADMIN)
- `GET /teams/search?keyword={keyword}` - Ricerca squadre

### Gestione Tornei (`/tournaments`)

- `GET /tournaments` - Lista tornei
- `POST /tournaments` - Crea torneo (ADMIN)
- `GET /tournaments/{id}` - Dettagli torneo
- `POST /tournaments/{tournamentId}/teams/{teamId}` - Iscrivi squadra
- `GET /tournaments/status/{status}` - Filtra per stato
- `GET /tournaments/upcoming` - Tornei futuri

### Gestione Partite (`/matches`)

- `GET /matches` - Lista partite
- `POST /matches` - Crea partita (ADMIN)
- `PUT /matches/{id}/result` - Aggiorna risultato (ADMIN)
- `GET /matches/today` - Partite odierne
- `GET /matches/tournament/{id}` - Partite per torneo
- `GET /matches/team/{id}` - Partite per squadra

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

## Configurazione

### File .env
```env
# Database Configuration
DB_NAME=tournament_football
DB_USERNAME=tournament_user
DB_PASSWORD=password
DB_ROOT_PASSWORD=rootpassword

# JWT Configuration
JWT_SECRET=mySecretKey12345678901234567890123456789012345678901234567890
JWT_EXPIRATION=86400000

# Server Configuration
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=docker

# JPA Configuration
SPRING_JPA_SHOW_SQL=true
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

### Sicurezza JWT
- **Algoritmo**: HS256 (HMAC con SHA-256)
- **Durata token**: 24 ore (configurabile)
- **Secret key**: Configurabile tramite variabili d'ambiente

### Permessi Endpoint
- **Pubblici** (`/auth/**`): Accesso libero
- **Autenticati**: Richiede token JWT valido
- **Solo ADMIN**: Operazioni amministrative
- **Proprietario o ADMIN**: Accesso ai propri dati

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

### Test API con Postman
Collection completa disponibile in `Tournament Football API.postman_collection.json`:

**Funzionalità:**
- Gestione automatica JWT
- Variabili dinamiche
- Workflow completo di test (12 step)
- Test regole di business
- Validazione autorizzazioni

**Utilizzo:**
1. Importare collection in Postman
2. Configurare `base_url` = `http://localhost:8080/api`
3. Eseguire "Complete Testing Workflow"

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

## Sviluppi Futuri

### Funzionalità Future
- Statistiche dettagliate (goal, assist, presenze, cartellini)
- Generazione calendario automatico
- Diversi tipi di torneo (eliminazione diretta, gironi)
- Export dati in PDF/Excel
- Sistema notifiche real-time
- Interfaccia web completa

### Miglioramenti Tecnici
- Architettura a microservizi
- Comunicazione asincrona
- Deployment cloud (AWS, Azure)

## Licenza

Progetto sviluppato per scopi educativi.

---

**Sviluppato utilizzando Spring Boot, MySQL e Docker**