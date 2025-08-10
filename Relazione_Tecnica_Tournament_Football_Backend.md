# Relazione Tecnica: Tournament Football Backend

## Indice

- [Introduzione e Visione del Progetto](#introduzione-e-visione-del-progetto)
- [Obiettivi](#obiettivi)
- [Scelte Tecniche e Architettura](#scelte-tecniche-e-architettura)
- [Organizzazione progetto](#organizzazione-progetto)
- [Strategia di Containerizzazione e Deployment](#strategia-di-containerizzazione-e-deployment)
- [Architettura Interna](#architettura-interna)
- [Come Eseguire il Progetto](#come-eseguire-il-progetto)
- [Database e Inizializzazione](#database-e-inizializzazione)
- [API Endpoints Principali - Come usare l'applicazione](#api-endpoints-principali---come-usare-lapplicazione)
- [Configurazione Ambiente](#configurazione-ambiente)
- [Sviluppi Futuri](#sviluppi-futuri)
- [Considerazioni Finali](#considerazioni-finali)

## Introduzione e Visione del Progetto

Ho sviluppato "**Tournament Football Backend**" come progetto per creare il backend di un'applicazione per gestire tornei di calcetto. L'idea è nata pensando a come digitalizzare la gestione di competizioni sportive che spesso vengono ancora organizzate con carta e penna. Il sistema che ho creato può essere usato in diversi modi:

- *Per organizzazioni sportive locali*: Un modo semplice per gestire tornei di calcetto tra amici o squadre amatoriali, tenendo traccia di squadre, partite e risultati.

- *Per centri sportivi*: Una soluzione più completa per chi deve gestire più tornei contemporaneamente con tante squadre e giocatori.

## Obiettivi

Gli obiettivi che mi sono posta nello sviluppo sono stati:

- Creare un'API REST che permetta di fare tutte le operazioni CRUD (creare, leggere, modificare, cancellare) su utenti, squadre, tornei e partite.
- Usare un database MySQL per salvare tutti i dati e implementare le relazioni tra le tabelle che abbiamo studiato nel corso (OneToOne, OneToMany, ManyToOne, ManyToMany).
- Implementare un sistema di login sicuro con token JWT e ruoli diversi (USER e ADMIN).
- Scrivere test per verificare che il codice funzioni bene (ho raggiunto oltre il 35% di copertura richiesta).
- Usare Docker per rendere facile l'installazione e l'esecuzione del progetto.
- Documentare tutte le API con Postman in modo che si possano testare facilmente

In sintesi, **l'obiettivo era creare un'API completa per gestire tornei di calcetto, con un sistema modulare per utenti, squadre, tornei e partite, usando tecnologie moderne e sicure**.

---

Una volta definita l'utilità dell'applicativo e gli obbiettivi da raggiungere, ho definito come farlo tecnicamente. Ho optato per Docker perché rende tutto più semplice da installare.
## Scelte Tecniche e Architettura

### Tecnologie Utilizzate
Il progetto è stato sviluppato utilizzando le seguenti tecnologie e strumenti:

#### Backend Framework
- **Spring Boot**: Versione **3.5.4**
- **Spring Security**: Autenticazione JWT e autorizzazione basata su ruoli
- **Spring Data JPA**: Persistenza dati e ORM
- **JWT**: Per i token di autenticazione

#### Database e Persistenza
- **MySQL**: Versione **8.0**, Database relazionale principale
- **H2**: Database temporaneo per i test
- **phpMyAdmin**: Per vedere e gestire il database facilmente

#### Build e Deployment
- **Maven**: Versione **3.9+** per gestione dipendenze e build
- **Docker**: Versione **20.10+** per containerizzazione
- **Docker Compose**: Versione **2.0+** per orchestrazione multi-container
- **Java**: Versione **21**

#### Testing
- **JUnit 5 + Mockito**: Per scrivere test automatici
- **Postman**: Utilizzato per testare le API
## Organizzazione progetto

L'applicazione si divide in 4 moduli principali che seguono il pattern MVC:

### Modulo User (Utenti)
**Cosa fa**: Gestisce gli utenti e l'autenticazione
- **Controller**: `AuthController`, `UserController`
- **Service**: `UserService`, `UserDetailsServiceImpl`
- **Repository**: `UserRepository`, `ProfileRepository`
- **Entità**: `User`, `Profile`
- **Relazioni JPA:** OneToOne (User ↔ Profile), ManyToMany (User ↔ Team)
- **Sicurezza:** JWT stateless, autorizzazione basata su ruoli (USER/ADMIN)
- **Funzionalità**: Autenticazione JWT, gestione profili, autorizzazioni, login, registrazione

### Modulo Team  (Squadre)
**Cosa fa**: Gestisce le squadre e i loro giocatori
- **Controller**: `TeamController`
- **Service**: `TeamService`
- **Repository**: `TeamRepository`
- **Entità**: `Team`
- **Relazioni JPA:** ManyToMany (Team ↔ Users e Tournament ↔ Teams)
- **Funzionalità**: Creare squadre, aggiungere/rimuovere giocatori, cercare squadre

### Modulo Tournament (Tornei)
**Cosa fa**: Gestisce i tornei dall'inizio alla fine
- **Controller**: `TournamentController`
- **Service**: `TournamentService`
- **Repository**: `TournamentRepository`
- **Entità**: `Tournament`
- **Relazioni JPA:** OneToMany (Tournament → Match), ManyToMany (User ↔ Team)
- **Stati Gestiti:** OPEN, IN_PROGRESS, COMPLETED, CANCELLED, SCHEDULED
- **Funzionalità**: Creare tornei, iscrivere squadre, gestire stati

### Modulo Match (Partite)
**Cosa fa**: Gestisce le partite e i risultati
- **Controller**: `MatchController`
- **Service**: `MatchService`
- **Repository**: `MatchRepository`
- **Entità**: `Match`
- **Relazioni JPA:** ManyToOne (Match → Teams (home/away) e Match → Tournament)
- **Stati Gestiti:** SCHEDULED, IN_PROGRESS, COMPLETED, POSTPONED, CANCELLED, TO_BE_SCHEDULED
- **Funzionalità**: Programmare partite, inserire risultati, consultare calendario
---

Stabilita l'architettura, per semplificare l'installazione e garantire che l'applicazione funzioni su qualsiasi computer ho usato Docker.
## Strategia di Containerizzazione e Deployment

### Architettura Multi-Container

L'adozione di Docker offre benefici significativi eliminando le problematiche di configurazione e garantendo comportamento uniforme su ambienti diversi. Con Docker, chiunque può far partire il progetto senza dover installare MySQL, configurare database o sistemare versioni diverse di Java.

```yaml
# docker-compose.yml - Orchestrazione Servizi
services:
  mysql:     # Database persistente con inizializzazione automatica
  app:       # Applicazione Spring Boot (build multi-stage)
  phpmyadmin: # Interface amministrazione database
```

Il docker-compose.yml orchestizza tre servizi:
1. **mysql**: Database MySQL 8.0 con inizializzazione automatica
2. **app**: Applicazione Spring Boot
3. **phpmyadmin**: Interface web per gestione database (porta 8081)

#### Mapping Porte
- **8080**: Applicazione Spring Boot
- **8081**: phpMyAdmin interface  
- **3306**: MySQL database (connessioni esterne)

Questa strategia di containerizzazione con docker porta i seguenti vantaggi implementativi:

- **Configurazione automatica:** Tutto si installa e configura da solo
- **Dati di test:** Il database parte già con utenti, squadre e tornei di esempio
- **Funziona ovunque:** Stesso comportamento su Windows, Mac, Linux

---

Definito l'ambiente di esecuzione, è necessario approfondire come l'applicazione sia strutturata internamente. L'architettura a strati organizza i componenti secondo responsabilità specifiche.
## Architettura Interna

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
### Organizzazione dei Package Principali
```
tournament-football-backend/
|-- src/
|   |-- main/
|   |   |-- java/
|   |   |   |-- com/
|   |   |       |-- tournament_football_backend/
|   |   |           |-- config/           # Configurazione sicurezza e JWT
|   |   |           |-- controller/       # Gestione richieste HTTP e definizione endpoint API RESTful.
|   |   |           |-- dto/              # Data Transfer Objects per trasferimento sicuro dati
|   |   |           |-- exception/        # Gestione eccezioni personalizzate
|   |   |           |-- model/            # Entità del dominio con relazioni JPA complete
|   |   |           |-- repository/       # Interfacce accesso dati con Spring Data JPA
|   |   |           |-- service/          # Logica di business principale e validazioni             
|   |   |           |-- TournamentFootballBackendApplication.java
|   |   |-- resources/
|   |       |-- application.properties    # Configurazione applicazione
|   |       |-- schema.sql                # Script definizione schema database completo
|   |       |-- data.sql                  # Script inizializzazione dati di test realistici
|   |-- test/
|       |-- java/                         # Test unitari completi
|-- .env                                  # Variabili d'ambiente
|-- Dockerfile                            # Container applicazione
|-- docker-compose.yml                    # Orchestrazione servizi
|-- pom.xml                               # Configurazione Maven
|-- Tournament Football API.postman_collection.json
|-- README.md
```

---
### Controller Layer (Livello Presentazione)

I controller gestiscono le richieste HTTP e orchestrano le chiamate ai servizi:
- **AuthController**: Gestisce login e registrazione utenti
- **TeamController**: CRUD squadre e gestione giocatori
- **TournamentController**: CRUD tornei e iscrizioni squadre
- **MatchController**: CRUD partite e gestione risultati

Ogni controller:
- Controlla che i dati in input siano validi con `@Valid`
- Verifica che l'utente abbia i permessi giusti con `@PreAuthorize`
- Gestisce gli errori in modo appropriato
- Restituisce le risposte in formato JSON con `ResponseEntity`

---
### Service Layer (Logica di Business)
I servizi contengono la logica principale dell'applicazione:

- **UserService**: Gestione utenti, profili, validazioni unicità
- **TeamService**: Gestione squadre, assegnazione giocatori
- **TournametService**: Gestione tornei, iscrizioni, validazioni date
- **MatchService**: Gestione partite, risultati, calendario
- **UserDetailsServiceImpl**: Si integra con Spring Security per l'autenticazione

---
### Repository Layer (Accesso al database)
Repository JPA per interazione con database (si occupano di "parlare" con il database):

- **UserRepository**:  Cerca utenti, gestisce login
- **TeamRepository**: Cerca squadre, trova squadre per giocatore
- **TournamentRepository**: Cerca tornei, filtra per stato/date
- **MatchRepository**:  Cerca partite, calcola statistiche
- **ProfileRepository**: Gestisce i profili utente

---
### Models (Rappresentazione dati)
Le entità rappresentano le tabelle del database:
- **User**: Rappresenta un utente registrato
- **Profile**: Rappresenta i dati personali di un utente
- **Match**: Rappresenta una partita
- **Team**: Rappresenta una squadra 
- **Tournament**: Rappresenta un torneo

---
### Implementazione Relazioni JPA (Relazioni tra tabelle)

Il sistema implementa **tutte le tipologie di relazione** richieste:
- **OneToOne**: User ↔ Profile (ogni utente ha un profilo)
- **OneToMany**: Tournament → Matches (un torneo ha tante partite)
- **ManyToOne**: Match → Teams (home/away) (una partita appartiene a due squadre), Match → Tournament (una partita appartiene a un torneo)
- **ManyToMany**: Team ↔ Users (una squadra ha tanti giocatori),, Tournament ↔ Teams (un torneo ha tante squadre)

---
### DTO (Data Transfer Objects)
La strategia DTO implementa **separazione** tra entità interne e oggetti di trasferimento, per controllare meglio i dati che entrano ed escono:

- **CreateDTO:** Per quando creo qualcosa di nuovo (POST)
- **UpdateDTO:** Per quando modifico qualcosa (PUT)
- **ResponseDTO:** Per controllare cosa restituisco (GET)
- **Security:** Evito di esporre dati sensibili come le password

---
### Eccezioni 
Per la gestione degli errori ho deciso di implementare una serie di eccezioni personalizzate che derivano dalla classe base `TournamentException`. Queste eccezioni sono suddivise in classi specifice per i diversi moduli:

- **TeamExceptions:** Eccezioni relative alla gestione delle squadre
- **UserExceptions:** Eccezioni relative alla gestione degli utenti
- **TournamentExceptions:** Eccezioni relative alla gestione dei tornei
- **MatchExceptions:** Eccezioni relative alla gestione delle partite
- **ValidationExceptions:** Eccezioni di validazione

La gestione centralizzata delle eccezioni è affidata alla classe **GlobalExceptionHandler**, che cattura le eccezioni di tipo **TournamentException** e **MethodArgumentNotValidException**. Per le eccezioni non gestite in modo specifico, viene fornita una risposta di errore generica `INTERNAL_SERVER_ERROR`.

### Sicurezza e autenticazione
Per la sicurezza ho usato:

- **WebSecurityConfig:** Configurazione generale di Spring Security
- **AuthTokenFilter:** Controlla il token JWT ad ogni richiesta
- **JwtUtils:** Crea e verifica i token JWT

#### Come funziona JWT
- **Algoritmo:** HS256 (HMAC con SHA-256)
- **Durata token:** 24 ore (configurabile via environment)
- **Secret key:** Configurabile tramite variabili d'ambiente (file .env)

#### Permessi degli endpoint
- **Pubblici** (`/auth/**`): Accesso libero per login/register
- **Autenticati**: Richiede token JWT valido
- **Solo ADMIN**: Solo gli amministratori possono fare certe operazioni
- **Proprietario o ADMIN**: Puoi vedere/modificare solo i tuoi dati (o tutto se sei admin)
---

### Test Automatici
**Copertura Raggiunta:** Superiore al 35% richiesto

| Test Suite | Focus Testing |
|------------|---------------|
| **MatchServiceTest** | Logica delle partite, controllo punteggi |
| **TeamServiceTest** | Gestione giocatori, ricerche |
| **TournamentServiceTest** | Stati dei tornei, logica complessa |
| **UserServiceTest** | Autenticazione, profili |
| **UserDetailsServiceImplTest** | Integrazione Spring Security |

**Come ho fatto i test:** Ho usato JUnit 5 + Mockito per simulare le dipendenze e testare vari scenari.

---
## Come Eseguire il Progetto
### Requisiti
Per eseguire il progetto è necessario avere installato sul proprio sistema **Docker 20.10+** e **Docker Compose 2.0+**.
### Esecuzione applicazione con Docker e Docker Compose
Passi da seguire:

1. **Clonazione del Repository**
    ```bash  
    git clone <repository-url>
    cd tournament-football-backend
    ```

2. **Avviare i servizi con Docker Compose:** 
    #### Avvia tutto l'ambiente (app + database + phpMyAdmin)
    ```
    docker-compose up --build -d
    ```
    L'applicazione sarà accessibile a:
    - **API**: http://localhost:8080/api
    - **phpMyAdmin**: http://localhost:8081
    - **Database**: http://localhost:3306

3. **Verificare che i container siano attivi (opzionale)**
    ```bash
    docker-compose ps
    ```

4. **Visualizzare tutti i log se qualcosa non va (opzionale)**
    ```bash
    docker-compose logs -f
    ```
   
5. **Arrestare i servizi:**
    ```bash
    docker-compose down
    ```
---
## Database e Inizializzazione

### Come Funziona l'Inizializzazione Database
1. **Docker Compose** avvia MySQL vuoto
2. **Spring Boot** si connette al database  
3. **Schema.sql** crea automaticamente tutte le tabelle
4. **Data.sql** popola il database con dati di test realistici
5. **JPA/Hibernate** gestisce le entità create

### Dati di Test Preconfigurati

**Account di Test:**
- **Admin**: `admin` / `password123`
- **Admin Secondario**: `matteo_bronze` / `password123`
- **Utenti Standard**: 
  - `mario_rossi` / `password123`
  - `luca_bianchi` / `password123`
  - `giuseppe_verdi` / `password123`

Le password nei dati di test sono già hashate con BCrypt, ma per comodità la password in chiaro è `password123` per tutti gli utenti di test.

**Dati Precaricati:**
- **15 utenti** con profili completi e dati realistici
- **12 squadre** con giocatori assegnati e nomi creativi
- **7 tornei** in diversi stati (OPEN, IN_PROGRESS, COMPLETED)
- **25+ partite** con risultati e calendario programmato
- **Relazioni complete** tra squadre-giocatori e tornei-squadre

### Schema Database
Il file `schema.sql` crea automaticamente:
- 7 tabelle principali (users, profiles, teams, tournaments, matches)
- 2 tabelle di relazione (team_players, tournament_teams)
- Vincoli di integrità referenziale
- Indici per performance ottimizzate

---
## API Endpoints Principali - Come usare l'applicazione

Ho creato **API REST complete** organizzate per funzionalità, seguendo gli standard REST e implementando sicurezza appropriata.

### Endpoint Autenticazione (`/auth`)

#### POST `/auth/login` - Login utente con generazione JWT
- **Descrizione**: Autentica utente e genera token JWT per accesso alle API protette
- **Autorizzazione**: Nessuna (endpoint pubblico)
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

#### POST `/auth/register` - Registrazione nuovo utente
- **Descrizione**: Registra nuovo utente nel sistema con ruolo USER di default
- **Autorizzazione**: Nessuna (endpoint pubblico)
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

---
### Endpoint Gestione Utenti (`/users`)

#### GET `/users` - Lista completa utenti registrati
- **Descrizione**: Recupera lista di tutti gli utenti del sistema
- **Autorizzazione**: Solo ADMIN
- **Headers**: `Authorization: Bearer <jwt_token>`
- **Response**
    - **Success (200 OK)**:
        ```json
            [
            {
                "id": 1,
                "username": "admin",
                "email": "admin@tournamentfootball.com",
                "role": "ROLE_ADMIN",
                "createdAt": "2025-01-01T10:00:00",
                "profile": {
                "firstName": "Admin",
                "lastName": "System",
                "city": "Milano"
                }
            }
            ]
        ```
    - **Error (403 Forbidden)**: Se non ADMIN

#### GET `/users/{id}` - Dettagli utente specifico
- **Descrizione**: Recupera dettagli completi di un utente incluso profilo
- **Autorizzazione**: ADMIN o proprietario dell'account
- **Headers**: `Authorization: Bearer <jwt_token>`
- **Response** 
    -  **Success (200 OK)**:
        ```json
        {
        "id": 2,
        "username": "mario_rossi",
        "email": "mario.rossi@email.com",
        "role": "ROLE_USER",
        "createdAt": "2025-01-01T10:30:00",
        "profile": {
            "firstName": "Mario",
            "lastName": "Rossi",
            "birthDate": "1990-05-15",
            "phone": "333-111-2222",
            "city": "Milano",
            "bio": "Calciatore appassionato"
        }
        }
        ```
    - **Error (404 Not Found)**: Se utente inesistente

#### PUT `/users/{id}/profile` - Aggiorna profilo personalizzato
- **Descrizione**: Aggiorna informazioni profilo personali utente
- **Autorizzazione**: ADMIN o proprietario dell'account
- **Headers**: `Authorization: Bearer <jwt_token>`
- **Request Body**:
    ```json
    {
    "firstName": "Mario",
    "lastName": "Rossi",
    "birthDate": "1990-05-15",
    "phone": "333-1234567",
    "city": "Milano",
    "bio": "Centrocampista con 10 anni di esperienza"
    }
    ```
- **Response** 
    - **Success (200 OK)**: UserDTO completo aggiornato
    - **Error (404 Not Found)**: Se utente inesistente

#### GET `/users/search?keyword={keyword}` - Ricerca utenti
- **Descrizione**: Ricerca utenti per username o email (solo ADMIN)
- **Autorizzazione**: Solo ADMIN
- **Headers**: `Authorization: Bearer <jwt_token>`
- **Query Parameters**: `keyword` (String) - Termine di ricerca
- **Response**
    - **Success (200 OK)**: Array UserDTO filtrati
    - **Error (403 Forbidden)**: Se non ADMIN

---
### Endpoint Gestione Squadre (`/teams`)

#### GET `/teams` - Lista squadre disponibili
- **Descrizione**: Recupera lista completa squadre registrate nel sistema
- **Autorizzazione**: Token JWT valido (USER o ADMIN)
- **Headers**: `Authorization: Bearer <jwt_token>`
- **Response**
    - **Success (200 OK)**:
        ```json
        [
        {
            "id": 1,
            "name": "Milan Lions",
            "createdAt": "2025-01-01T11:00:00",
            "numberOfPlayers": 5,
            "players": [
            {
                "id": 2,
                "username": "mario_rossi",
                "email": "mario.rossi@email.com"
            }
            ]
        }
        ]
        ```
    - **Error (401 Unauthorized)**: Se token non valido

#### POST `/teams` - Crea squadra
- **Descrizione**: Registra nuova squadra nel sistema
- **Autorizzazione**: USER o ADMIN
- **Headers**: `Authorization: Bearer <jwt_token>`
- **Request Body**:
    ```json
    {
    "name": "Nuova Squadra FC"
    }
    ```
- **Response**
    - **Success (201 Created)**:
        ```json
        {
        "id": 3,
        "name": "Nuova Squadra FC",
        "createdAt": "2025-01-15T14:30:00",
        "numberOfPlayers": 0,
        "players": []
        }
        ```
    - **Error (400 Bad Request)**: `{ "error": "Team name already exists" }`

#### POST `/teams/{teamId}/players/{playerId}` - Aggiungi giocatore
- **Descrizione**: Aggiunge giocatore al roster della squadra specificata
- **Autorizzazione**: Solo ADMIN
- **Headers**: `Authorization: Bearer <jwt_token>`
- **Path Parameters**: 
  - `teamId` (Long) - ID della squadra
  - `playerId` (Long) - ID del giocatore/utente
- **Response** 
    - **Success (200 OK)**:
        ```json
        {
        "message": "Player added successfully",
        "team": {
            "id": 1,
            "name": "Milan Lions",
            "numberOfPlayers": 6
        }
        }
        ```
    - **Error (409 Conflict)**: `{ "error": "Player already in team" }`
    - **Error (404 Not Found)**: `{ "error": "Team or player not found" }`

#### GET `/teams/search?keyword={keyword}` - Ricerca squadre
- **Descrizione**: Ricerca squadre per nome utilizzando keyword parziale
- **Autorizzazione**: Token JWT valido (USER o ADMIN)
- **Headers**: `Authorization: Bearer <jwt_token>`
- **Query Parameters**: `keyword` (String) - Nome squadra parziale
- **Response Success (200 OK)**:
    ```json
    [
        {
            "id": 1,
            "name": "Milan Lions",
            "numberOfPlayers": 5
        }
    ]
    ```

---
### Endpoint Gestione Tornei (`/tournaments`)

#### GET `/tournaments` - Lista tornei disponibili
- **Descrizione**: Recupera tutti i tornei con dettagli e squadre partecipanti
- **Autorizzazione**: Token JWT valido (USER o ADMIN)
- **Headers**: `Authorization: Bearer <jwt_token>`
- **Response Success (200 OK)**:
    ```json
    [
    {
        "id": 1,
        "name": "Coppa Italia 2025",
        "description": "Torneo nazionale amatoriale",
        "startDate": "2025-03-01",
        "endDate": "2025-03-31",
        "maxTeams": 16,
        "status": "OPEN",
        "numberOfRegisteredTeams": 8,
        "participatingTeams": [
        {
            "id": 1,
            "name": "Milan Lions",
            "numberOfPlayers": 5
        }
        ]
    }
    ]
    ```

#### POST `/tournaments` - Crea torneo
- **Descrizione**: Crea nuovo torneo nel sistema (solo amministratori)
- **Autorizzazione**: Solo ADMIN
- **Headers**: `Authorization: Bearer <jwt_token>`
- **Request Body**:
    ```json
    {
    "name": "Torneo Primavera 2025",
    "description": "Competizione primaverile per squadre amatoriali",
    "startDate": "2025-04-01",
    "endDate": "2025-04-30",
    "maxTeams": 16
    }
    ```
- **Response**
    - **Success (201 Created)**:
        ```json
        {
        "id": 2,
        "name": "Torneo Primavera 2025",
        "description": "Competizione primaverile per squadre amatoriali",
        "startDate": "2025-04-01",
        "endDate": "2025-04-30",
        "maxTeams": 16,
        "status": "SCHEDULED",
        "numberOfRegisteredTeams": 0,
        "participatingTeams": []
        }
        ```
    - **Error (400 Bad Request)**: `{ "error": "Start date must be before end date" }`

#### POST `/tournaments/{tournamentId}/teams/{teamId}` - Iscrivi squadra
- **Descrizione**: Iscrive squadra al torneo specificato
- **Autorizzazione**: USER o ADMIN
- **Headers**: `Authorization: Bearer <jwt_token>`
- **Path Parameters**:
  - `tournamentId` (Long) - ID del torneo
  - `teamId` (Long) - ID della squadra
- **Response**
    - **Success (200 OK)**:
        ```json
        {
        "message": "Team registered successfully",
        "tournament": {
            "id": 1,
            "name": "Coppa Italia 2025",
            "numberOfRegisteredTeams": 9
        }
        }
        ```
    - **Error (409 Conflict)**: `{ "error": "Tournament is full" }`
    - **Error (400 Bad Request)**: `{ "error": "Team already registered" }`

#### GET `/tournaments/status/{status}` - Filtra per stato
- **Descrizione**: Filtra tornei per stato specifico
- **Autorizzazione**: Token JWT valido (USER o ADMIN)
- **Headers**: `Authorization: Bearer <jwt_token>`
- **Path Parameters**: `status` - OPEN, IN_PROGRESS, COMPLETED, CANCELLED, SCHEDULED
- **Response:**
    - **Success (200 OK)**: Array TournamentDTO filtrati per stato
    - **Error (400 Bad Request)**: `{ "error": "Invalid status value" }`

#### GET `/tournaments/upcoming` - Tornei futuri
- **Descrizione**: Recupera tornei con data inizio futura
- **Autorizzazione**: Token JWT valido (USER o ADMIN)
- **Headers**: `Authorization: Bearer <jwt_token>`
- **Response Success (200 OK)**:
    ```json
    [
        {
            "id": 2,
            "name": "Torneo Primavera 2025",
            "startDate": "2025-04-01",
            "endDate": "2025-04-30",
            "status": "SCHEDULED",
            "numberOfRegisteredTeams": 4
        }
    ]
    ```

---
### Endpoint Gestione Partite (`/matches`)

#### GET `/matches` - Lista partite
- **Descrizione**: Recupera calendario completo di tutte le partite
- **Autorizzazione**: Token JWT valido (USER o ADMIN)
- **Headers**: `Authorization: Bearer <jwt_token>`
- **Response Success (200 OK)**:
    ```json
    [
        {
            "id": 1,
            "homeTeamId": 1,
            "awayTeamId": 2,
            "tournamentId": 1,
            "homeTeamName": "Milan Lions",
            "awayTeamName": "Rome Eagles",
            "tournamentName": "Coppa Italia 2025",
            "matchDate": "2025-03-05T15:00:00",
            "homeGoals": 2,
            "awayGoals": 1,
            "status": "COMPLETED",
            "result": "2 - 1"
        }
    ]
    ```

#### POST `/matches` - Crea partita
- **Descrizione**: Programma nuova partita nel torneo specificato
- **Autorizzazione**: Solo ADMIN
- **Headers**: `Authorization: Bearer <jwt_token>`
- **Request Body**:
    ```json
    {
    "homeTeamId": 1,
    "awayTeamId": 2,
    "tournamentId": 1,
    "matchDate": "2025-04-15T16:00:00"
    }
    ```
- **Response:**
    - **Success (201 Created)**:
        ```json
        {
        "id": 5,
        "homeTeamId": 1,
        "awayTeamId": 2,
        "tournamentId": 1,
        "homeTeamName": "Milan Lions",
        "awayTeamName": "Rome Eagles",
        "tournamentName": "Coppa Italia 2025",
        "matchDate": "2025-04-15T16:00:00",
        "homeGoals": null,
        "awayGoals": null,
        "status": "SCHEDULED",
        "result": null
        }
        ```
    - **Error (400 Bad Request)**: `{ "error": "Teams must be different" }`
    - **Error (404 Not Found)**: `{ "error": "Tournament or teams not found" }`

#### PUT `/matches/{id}/result` - Aggiorna risultato finale
- **Descrizione**: Inserisce risultato finale partita e cambia stato a COMPLETED
- **Autorizzazione**: Solo ADMIN
- **Headers**: `Authorization: Bearer <jwt_token>`
- **Path Parameters**: `id` (Long) - ID della partita
- **Request Body**:
    ```json
    {
    "homeGoals": 3,
    "awayGoals": 1
    }
    ```
- **Response:**
    - **Success (200 OK)**:
        ```json
        {
        "id": 5,
        "homeTeamName": "Milan Lions",
        "awayTeamName": "Rome Eagles",
        "homeGoals": 3,
        "awayGoals": 1,
        "status": "COMPLETED",
        "result": "3 - 1"
        }
        ```
    - **Error (400 Bad Request)**: `{ "error": "Goals cannot be negative" }`
    - **Error (404 Not Found)**: `{ "error": "Match not found" }`

#### GET `/matches/today` - Partite odierne
- **Descrizione**: Recupera partite programmate per la data odierna
- **Autorizzazione**: Token JWT valido (USER o ADMIN)
- **Headers**: `Authorization: Bearer <jwt_token>`
- **Response Success (200 OK)**:
    ```json
    [
        {
            "id": 3,
            "homeTeamName": "Inter Milan",
            "awayTeamName": "AC Milan",
            "tournamentName": "Derby Cup",
            "matchDate": "2025-01-15T20:30:00",
            "status": "SCHEDULED"
        }
    ]
    ```

#### GET `/matches/tournament/{id}` - Partite per torneo
- **Descrizione**: Recupera tutte le partite di un torneo specifico
- **Autorizzazione**: Token JWT valido (USER o ADMIN)
- **Headers**: `Authorization: Bearer <jwt_token>`
- **Path Parameters**: `id` (Long) - ID del torneo
- **Response:**
    - **Success (200 OK)**: Array MatchDTO del torneo specificato
    - **Error (404 Not Found)**: `{ "error": "Tournament not found" }`

---
### Codici di Stato HTTP Utilizzati

| Codice | Significato | Utilizzo |
|--------|-------------|----------|
| **200 OK** | Operazione riuscita | Operazioni riuscite (GET, PUT) |
| **201 Created** | Risorsa creata | Quando creo qualcosa di nuovo (POST)|
| **400 Bad Request** | Dati input non validi | Quando i dati inviati non sono validis |
| **401 Unauthorized** | Token mancante/invalido | Quando manca o è sbagliato il token |
| **403 Forbidden** | Autorizzazioni insufficienti | Quando non hai i permessi |
| **404 Not Found** | Risorsa inesistente | Quando cerchi qualcosa che non esiste |
| **409 Conflict** | Conflitto| Quando provi a fare qualcosa di non permesso |

---
### Test delle API con Postman
Per facilitare i test delle API, è stata creata una Collection di Postman.
La Collection include tutte le richieste necessarie per testare le funzionalità dell'applicazione. La Collection è disponibile nel file `Tournament Football API.postman_collection.json` nella root del progetto.

**Importare Collection su Postman**
Per importare la Collection in Postman, basta seguire questi passaggi:
1. Aprire Postman.
2. Cliccare su "Importa" nella barra laterale sinistra.
3. Selezionare il file JSON della Collection.
4. Cliccare su "Importa".
5. La Collection sarà disponibile nella barra laterale sinistra di Postman.

#### Cosa include la collection
La Collection `Tournament Football API.postman_collection.json` include:

**Funzionalità Automatizzate:**
- **Gestione automatica del JWT**: Dopo il login, il token viene salvato e usato automaticamente
- **Variabili dinamiche**: Gli ID vengono estratti automaticamente e usati nelle richieste successive
- **Script automatici:**: Headers e validazioni automatiche

**Complete Testing Workflow:**
Ho creato una sequenza di 12 step che testa tutto dall'inizio alla fine::
1. Login Admin → Salva il token
2. User registration → Salva l'ID  
3. Team creation (home/away) → Salva gli ID
4. Tournament creation → Salva l'ID
5. Team registrations → Testa la logica business
6. Match creation → Salva l'ID
7. Result update → Valida il tutto
8. Profile update → Testa la gestione utenti
9. Data verification → Controlla la coerenza

**Procedura di Utilizzo:**
1. Importare Collection in Postman
2. Configurare `base_url` = `http://localhost:8080/api`
3. Eseguire "Complete Testing Workflow" per test automatico
4. Oppure usa le singole richieste per test specifici

**Test delle regole di business:**
- **Torneo pieno**: Prova a iscrivere più squadre del limite massimo
- **Squadra contro se stessa:** Prova a creare una partita con la stessa squadra casa e trasferta
- **Autorizzazioni**: Test accessi USER vs ADMIN
- **Token Scaduto**: Gestione token non validi
- **Dati non validi**: Input con errori validi
---
## Configurazione Ambiente

### File di configurazione (.env)
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

---
## Sviluppi Futuri

### Funzionalità Future

In un futuro potranno essere implementate funzionalità quali:
- Statistiche dettagliate per partite, tornei, giocatori (goal, assist, presenze, cartellini)
- Categorie diverse per suddividere i tornei
- Generazione calendario automatico con bilanciamento tra orari/campi
- Diversi tipi di torneo (eliminazione diretta, gironi, ecc.)
- Categorie diverse per suddividere i tornei
- Export dei dati in pdf/excel
- Possibilità di iscrizione ad un determinato torneo da parte dell'utente
- Sistema di notifiche real-time per aggiornamento risultati partite, classifica e reminder partite
- Interfaccia web completa per non dover usare solo le API

###  Miglioramenti tecnici
Dal punto di vista tecnico si potrebbe evolvere verso:
- **Architettura a microservizi:** I moduli sono già separati, quindi sarebbe relativamente facile dividerli in servizi indipendenti
- **Comunicazione asincrona:** Aggiungere un sistema di messaggi per comunicazioni non immediate
- **Deployment cloud:** Preparare il tutto per funzionare su cloud come AWS o Azure
---
## Considerazioni Finali

Il progetto **Tournament Football Backend** è stata un'esperienza molto formativa che mi ha permesso di mettere in pratica molti concetti studiati durante il corso. Ho utilizzato tecnologie moderne come **Java 21, Spring Boot 3.5.4 e Docker** per creare un sistema funzionante e completo.

### Cosa ho imparato
Durante lo sviluppo ho approfondito:

- Come strutturare un'applicazione seguendo buone pratiche
- L'uso di Spring Security per gestire autenticazione e autorizzazioni
- Le relazioni JPA e come implementarle correttamente
- L'utilizzo dei DTO per la movimentazione dei dati
- L'importanza dei test automatici per verificare che tutto funzioni
- Come Docker semplifica il deployment e la condivisione del progetto

### Risultati ottenuti
Il sistema che ho creato è:

- **Funzionante:** Tutte le API lavorano correttamente
- **Sicuro:** Implementa autenticazione JWT e controlli di autorizzazione
- **Testato:** Copertura di test superiore al 35% richiesto
- **Documentato:** API completamente documentate con esempi pratici
- **Facile da installare:** Basta un comando Docker per far partire tutto

### Utilità pratica
Il progetto può essere utilizzato per gestire tornei di calcetto, con tutte le funzionalità necessarie per organizzare competizioni dall'inizio alla fine.

La documentazione completa e la Postman Collection rendono il sistema facile da capire e testare, dimostrando un approccio professionale allo sviluppo software.