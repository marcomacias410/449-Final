# Game Backlog Tracker - CPSC 449 Final Project

A secure REST API for tracking your video game library: games you're playing, ones in your backlog, completed, dropped, or on hold. Each user has a private library - they can only see and modify their own games.

**Tech stack:** Java 21 · Spring Boot 3.3 · Spring Security 6 · Spring Data JPA · MySQL 8 · JJWT 0.12.x · Docker (multi-stage)

**Domain:** Game Backlog Tracker
**Entities & relationship:** `User` ⇄ `Game` - One-to-Many (one user owns many games; each game belongs to exactly one user). Implemented with a `user_id` foreign key on the `games` table.

---

## Prerequisites

| Tool | Version |
|---|---|
| Java (only if building outside Docker) | 21 |
| Docker Desktop | 24 or newer |
| MySQL 8 (running on the host) | 8.x |
| Postman | latest |

You do **not** need Maven installed locally - the Docker image builds the JAR itself.

### Set up MySQL on the host

Start MySQL on `localhost:3306` with a `root` user and a password you know. The app will create the `gamebacklog` database itself on first run (`createDatabaseIfNotExist=true` is in the JDBC URL), so you don't need to create the schema manually.

You can verify MySQL is reachable with:
```bash
mysql -u root -p -h 127.0.0.1 -P 3306 -e "SELECT 1;"
```

---

## Build the Docker image

From the project root (the directory containing `pom.xml` and `Dockerfile`):

```bash
docker build -t game-backlog-tracker:1.0 .
```

The first build downloads Maven dependencies and takes a few minutes. Subsequent builds are much faster thanks to layer caching.

---

## Run the application from Docker

Replace `YOURPASS` with your actual MySQL root password:

```bash
docker run -d --name game-backlog \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://host.docker.internal:3306/gamebacklog?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true" \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=YOURPASS \
  -e JWT_SECRET=ThisIsAVerySecureSecretKeyForJwtSigningHS256AlgorithmCpsc449 \
  game-backlog-tracker:1.0
```


### Verify the container started

```bash
docker logs game-backlog
```

You should see the Spring Boot banner and a line like:
```
Tomcat started on port 8080 (http) with context path '/'
Started GameBacklogApplication in X.XXX seconds
```

### Stop and remove the container

```bash
docker stop game-backlog && docker rm game-backlog
```

---

## API reference

Base URL: `http://localhost:8080`

### Auth (public)

| Method | Path | Body | Success | Error |
|---|---|---|---|---|
| POST | `/api/auth/register` | `{username, email, password}` | 201 + JWT | 409 if email taken |
| POST | `/api/auth/login` | `{email, password}` | 200 + JWT | 401 on bad creds |

### Games (protected - requires `Authorization: Bearer <token>`)

| Method | Path | Description |
|---|---|---|
| POST | `/api/games` | Create a game. `userId` comes from the JWT. → 201 |
| GET | `/api/games` | List **only** the authenticated user's games. → 200 |
| GET | `/api/games/{id}` | One game. → 200, 404, or 403 if not yours |
| PUT | `/api/games/{id}` | Update a game you own. → 200, 404, or 403 |
| DELETE | `/api/games/{id}` | Delete a game you own. → 204, 404, or 403 |

### HTTP status codes used

| Code | Meaning |
|---|---|
| 200 | Successful GET / PUT |
| 201 | Resource created |
| 204 | Resource deleted |
| 400 | Validation failure / malformed body |
| 401 | Missing, invalid, or expired token |
| 403 | Valid token, but you don't own the resource |
| 404 | Resource doesn't exist |
| 409 | Email already registered |
| 500 | Unexpected server error (responses are still clean JSON) |

All error responses look like this:
```json
{
  "status": 404,
  "message": "Game with id 99 not found",
  "timestamp": "2026-04-17T10:30:00"
}
```

---

## Postman examples

### 1. Register a new user
```
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "alex",
  "email": "alex@example.com",
  "password": "supersecret123"
}
```
**Response - 201 Created**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "userId": 1,
  "email": "alex@example.com",
  "username": "alex",
  "expiresInMs": 86400000
}
```

### 2. Log in
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "alex@example.com",
  "password": "supersecret123"
}
```
**Response - 200 OK** (same shape as register)

### 3. Create a game (protected)
```
POST http://localhost:8080/api/games
Authorization: Bearer <paste-the-token>
Content-Type: application/json

{
  "title": "Hollow Knight",
  "platform": "PC",
  "genre": "Metroidvania",
  "status": "PLAYING",
  "hoursPlayed": 12.5,
  "rating": 9,
  "notes": "Loving the atmosphere"
}
```
**Response - 201 Created**

### 4. List my games (protected)
```
GET http://localhost:8080/api/games
Authorization: Bearer <token>
```
**Response - 200 OK** - array of games owned by the authenticated user only.

### 5. Update a game (protected)
```
PUT http://localhost:8080/api/games/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Hollow Knight",
  "platform": "PC",
  "genre": "Metroidvania",
  "status": "COMPLETED",
  "hoursPlayed": 38.0,
  "rating": 10,
  "notes": "Beat the Radiance"
}
```

### 6. Delete a game (protected)
```
DELETE http://localhost:8080/api/games/1
Authorization: Bearer <token>
```
**Response - 204 No Content**

### 7. Demonstrate auth failure
A request to `/api/games` with **no** token returns 401:
```json
{
  "status": 401,
  "message": "Authentication required: missing, invalid, or expired token",
  "timestamp": "2026-04-17T10:30:00"
}
```

### 8. Demonstrate data isolation
Register a second user, get their token, and try to GET game id `1` (which belongs to the first user) using the second user's token. You'll get a 403:
```json
{
  "status": 403,
  "message": "You do not have permission to access this game",
  "timestamp": "2026-04-17T10:30:00"
}
```

---


