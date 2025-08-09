# Tournament Football Backend

Un sistema completo per la gestione di tornei di calcetto, sviluppato con Spring Boot e containerizzato con Docker.

## Indice

- [Descrizione del Progetto](#descrizione-del-progetto)
- [Tecnologie Utilizzate](#tecnologie-utilizzate)
- [Architettura](#architettura)
- [Requisiti](#requisiti)
- [Installazione e Avvio](#installazione-e-avvio)
- [Configurazione](#configurazione)
- [API Documentation](#api-documentation)
- [Database](#database)
- [Test](#test)
- [Collection Postman](#collection-postman)
- [Sviluppi Futuri](#sviluppi-futuri)

## Descrizione del Progetto

**Tournament Football Backend** è un'API REST completa per la gestione di tornei di calcetto. Il sistema permette di:

- Gestire utenti con profili personalizzati e sistema di autenticazione JWT
- Creare e amministrare squadre con gestione dei giocatori
- Organizzare tornei con iscrizioni e controllo degli stati
- Programmare partite e registrare risultati
- Gestire permessi differenziati per utenti e amministratori

### Casi d'Uso

- **Organizzazioni sportive locali**: Gestione semplice di tornei amatoriali
- **Centri sportivi**: Soluzione completa per gestire più tornei contemporaneamente
- **Comunità di giocatori**: Piattaforma per organizzare competizioni tra amici

## Tecnologie Utilizzate

### Backend
- **Java 21**
- **Spring Boot 3.5.4**
- **Spring Security** - Autenticazione JWT e autorizzazione
- **Spring Data JPA** - Persistenza dati
- **Maven 3.9+** - Gestione dipendenze

### Database
- **MySQL 8.0** - Database principale
- **H2** - Database per test
- **phpMyAdmin** - Interfaccia di gestione database

### DevOps & Testing
- **Docker & Docker Compose** - Containerizzazione
- **JUnit 5 + Mockito** - Test automatici (>60% copertura)
- **Postman** - Documentazione e test API

## Architettura

Il progetto segue il pattern **MVC** organizzato in 4 moduli principali:

```
┌─────────────────┐
│   Controllers   │ ← Gestione richieste HTTP
├─────────────────┤
│    Services     │ ← Logica di business
├─────────────────┤
│  Repositories   │ ← Accesso ai dati
├─────────────────┤
│     Models      │ ← Entità del database
└─────────────────┘
```

### Moduli Applicativi

1. **User Module**: Gestione utenti, autenticazione JWT, profili
2. **Team Module**: Gestione squadre e giocatori
3. **Tournament Module**: Gestione tornei e iscrizioni
4. **Match Module**: Gestione partite e risultati

### Relazioni JPA Implementate

- **OneToOne**: User ↔ Profile
- **OneToMany**: Tournament → Matches
- **ManyToOne**: Match → Teams, Match → Tournament
- **ManyToMany**: Team ↔ Users, Tournament ↔ Teams

## Requisiti

- **Docker 20.10+**
- **Docker Compose 2.0+**

## Installazione e Avvio

### 1. Clonazione del Repository
```bash
git clone <repository-url>
cd tournament-football-backend
```

### 2. Avvio con Docker Compose
```bash
# Avvia tutto l'ambiente (app + database + phpMyAdmin)
docker-compose up --build -d
```

### 3. Accesso ai Servizi
- **API**: http://localhost:8080/api
- **phpMyAdmin**: http://localhost:8081
- **Database**: localhost:3306

### 4. Verifica Stato (Opzionale)
```bash
# Controlla che i container siano attivi
docker-compose ps

# Visualizza i log
docker-compose logs -f
```

### 5. Arresto Servizi
```bash
docker-compose down
```

## Configurazione

Il file `.env` contiene tutte le configurazioni principali:

```env
# Database Configuration
DB_NAME=tournament_football
DB_USERNAME=tournament_user
DB_PASSWORD=password

# JWT Configuration
JWT_SECRET=mySecretKey12345678901234567890123456789012345678901234567890
JWT_EXPIRATION=86400000

# Server Configuration
SERVER_PORT=8080
```

## API Documentation

### Autenticazione

#### POST `/auth/login` - Login utente
```json
{
  "username": "admin",
  "password": "password123"
}
```

#### POST `/auth/register` - Registrazione
```json
{
  "username": "nuovo_utente",
  "email": "utente@email.com",
  "password": "password123"
}
```

### Gestione Squadre

#### GET `/teams` - Lista squadre
*Richiede: Token JWT*

#### POST `/teams` - Crea squadra
```json
{
  "name": "Nuova Squadra FC"
}
```

#### POST `/teams/{teamId}/players/{playerId}` - Aggiungi giocatore
*Richiede: Ruolo ADMIN*

### Gestione Tornei

#### GET `/tournaments` - Lista tornei
*Richiede: Token JWT*

#### POST `/tournaments` - Crea torneo
*Richiede: Ruolo ADMIN*
```json
{
  "name": "Torneo Primavera 2025",
  "description": "Competizione primaverile",
  "startDate": "2025-04-01",
  "endDate": "2025-04-30",
  "maxTeams": 16
}
```

#### POST `/tournaments/{tournamentId}/teams/{teamId}` - Iscrivi squadra

### Gestione Partite

#### GET `/matches` - Lista partite
*Richiede: Token JWT*

#### POST `/matches` - Crea partita
*Richiede: Ruolo ADMIN*
```json
{
  "homeTeamId": 1,
  "awayTeamId": 2,
  "tournamentId": 1,
  "matchDate": "2025-04-15T16:00:00"
}
```

#### PUT `/matches/{id}/result` - Aggiorna risultato
*Richiede: Ruolo ADMIN*
```json
{
  "homeGoals": 3,
  "awayGoals": 1
}
```

### Codici di Stato HTTP

| Codice | Significato | Utilizzo |
|--------|-------------|----------|
| 200 | OK | Operazioni riuscite |
| 201 | Created | Risorsa creata |
| 400 | Bad Request | Dati input non validi |
| 401 | Unauthorized | Token mancante/invalido |
| 403 | Forbidden | Autorizzazioni insufficienti |
| 404 | Not Found | Risorsa inesistente |
| 409 | Conflict | Operazione non permessa |

## Database

### Inizializzazione Automatica

Il database viene popolato automaticamente con:
- **15 utenti** con profili completi
- **12 squadre** con giocatori assegnati
- **7 tornei** in diversi stati
- **25+ partite** con risultati e calendario

### Account di Test Preconfigurati

| Username | Password | Ruolo |
|----------|----------|-------|
| `admin` | `password123` | ADMIN |
| `matteo_bronze` | `password123` | ADMIN |
| `mario_rossi` | `password123` | USER |
| `luca_bianchi` | `password123` | USER |
| `giuseppe_verdi` | `password123` | USER |

### Accesso phpMyAdmin
- URL: http://localhost:8081
- Server: `mysql`
- Username: `tournament_user`
- Password: `password`

## Test

Il progetto include test automatici completi con **copertura del 63%**:

### Suite di Test
- **MatchServiceTest**: 30+ test per logica partite
- **TeamServiceTest**: 25+ test per gestione squadre
- **TournamentServiceTest**: 25+ test per logica tornei
- **UserServiceTest**: 30+ test per autenticazione
- **UserDetailsServiceImplTest**: 10+ test integrazione Spring Security

## Collection Postman

Il progetto include una Collection Postman completa per testare tutte le API:

### Importazione
1. Aprire Postman
2. Importare il file `Tournament Football API.postman_collection.json`

### Funzionalità Automatizzate
- **Gestione JWT automatica**: Token salvato dopo login
- **Variabili dinamiche**: ID estratti automaticamente
- **Workflow completo**: 12 step di test end-to-end

### Test Workflow Completo
La Collection include un workflow automatico che testa:
1. Login Admin
2. Registrazione utente
3. Creazione squadre
4. Creazione torneo
5. Iscrizione squadre
6. Creazione partite
7. Aggiornamento risultati
8. Verifiche finali

## Sviluppi Futuri

### Funzionalità Pianificate
- Statistiche dettagliate giocatori (goal, assist, presenze)
- Generazione automatica calendario partite
- Diversi formati torneo (eliminazione diretta, gironi)
- Sistema notifiche real-time
- Interfaccia web completa
- Export dati in PDF/Excel

### Miglioramenti Tecnici
- Architettura a microservizi
- Comunicazione asincrona
- Deployment cloud (AWS/Azure)
- Cache distribuita (Redis)
- Monitoring e logging avanzati

---

**Sviluppato usando Spring Boot e Docker**