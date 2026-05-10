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

You should see the Spring Boot banner and a line confirming Tomcat started on port 8080:

![Spring Boot startup banner in docker logs](/images/docker-logs-banner.png)

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

All error responses follow this format:

![Example clean error response in JSON](/images/error-response-example.png)

---

## Postman examples

### 1. Register a new user

`POST http://localhost:8080/api/auth/register` — sends username, email, and password. The server hashes the password with BCrypt and returns a JWT.

![Register request and 201 Created response](/images/01-register.png)

### 2. Log in

`POST http://localhost:8080/api/auth/login` — sends email and password. Returns a fresh JWT on success.

![Login request and 200 OK response](/images/02-login.png)

### 3. Create a game (protected)

`POST http://localhost:8080/api/games` with the Bearer token attached. The `userId` is taken from the JWT, never the request body.

![Create Game request and 201 Created response](/images/03-create-game.png)

### 4. List my games (protected)

`GET http://localhost:8080/api/games` — returns an array containing only the authenticated user's games.

![Get All Games request and 200 OK response with games array](/images/04-get-all-games.png)

### 5. Update a game (protected)

`PUT http://localhost:8080/api/games/{id}` — only the owner may update.

![Update Game request and 200 OK response](/images/05-update-game.png)

### 6. Delete a game (protected)

`DELETE http://localhost:8080/api/games/{id}` — only the owner may delete. Returns 204 No Content.

![Delete Game request and 204 No Content response](/images/06-delete-game.png)

---
