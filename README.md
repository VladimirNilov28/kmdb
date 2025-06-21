# KMDB

KMDB is a simple movies API that allows you to store and manage information about movies, including their genres and the actors who starred in them.

This API supports:
- Adding new movies to the database
- Updating existing entries
- Retrieving specific movies or filtering by genre or actor
- Managing genres and actors separately

## Installation

Clone the repository from [Gitea](https://gitea.kood.tech/vladimirnilov/kmdb.git) and build the project using the Gradle Wrapper:

```bash
# Clone the repository
git clone https://gitea.kood.tech/vladimirnilov/kmdb.git
cd kmdb

# Build the project
./gradlew build
```

For this project, you should have Java 21:

```bash
# Ubuntu/Debian
sudo apt install openjdk-21-jdk

# Arch
sudo pacman -S jdk21-openjdk

# macOS
brew install openjdk@21
```

## Usage

To launch the application:

```bash
./gradlew bootRun
```

This will:
- Download all required dependencies
- Compile the source code
- Start the Spring Boot application

Application will be available at:  
`http://localhost:8080/`

---

## API Overview

### Endpoints
- `GET /movies` – list all movies
- `GET /actors` – list all actors
- `GET /genres` – list all genres

---

## Movies

### Search by name
```http
GET /movies/search?find=Inception
```

### Filter movies
- By genres:
  ```http
  GET /movies?filter=genre:Action,Drama
  ```
- By release year:
  ```http
  GET /movies?filter=releaseYear:1999
  ```
- By actors:
  ```http
  GET /movies?filter=actor:Keanu Reeves,Carrie-Anne Moss
  ```

### Create movie
```http
POST /movies
Content-Type: application/json

{
  "movieName": "The Matrix",
  "releaseYear": 1999,
  "genres": [{"id": 1}],
  "actors": [{"id": 1}, {"id": 2}]
}
```

### Partially update movie
```http
PATCH /movies/1
Content-Type: application/json

{
  "releaseYear": 2000
}
```

---

## Actors

### Filter actors by movie
```http
GET /actors?filter=movie:The Matrix
```

### Create actor
```http
POST /actors
Content-Type: application/json

{
  "name": "Keanu Reeves",
  "birthDate": "1964-09-02"
}
```

### Partially update actor
```http
PATCH /actors/1
Content-Type: application/json

{
  "name": "K. Reeves"
}
```

---

## Genres

> Note: Filtering is not supported for genres.

### Create genre
```http
POST /genres
Content-Type: application/json

{
  "genre": "Sci-Fi"
}
```

### Partially update genre
```http
PATCH /genres/1
Content-Type: application/json

{
  "genre": "Action"
}
```

---

## POST examples

**Create a new movie:**
```
POST /movies
Content-Type: application/json

{
  "movieName": "The Matrix",
  "releaseYear": 1999,
  "genres": [{"id": 1}],
  "actors": [{"id": 1}, {"id": 2}]
}
```

**Create a new actor:**
```
POST /actors
Content-Type: application/json

{
  "name": "Keanu Reeves",
  "birthDate": "1964-09-02"
}
```

**Create a new genre:**
```
POST /genres
Content-Type: application/json

{
  "genre": "Action"
}
```

---

## PATCH examples

**Update a movie's release year:**
```
PATCH /movies/1
Content-Type: application/json

{
  "releaseYear": 2000
}
```

**Update an actor's name:**
```
PATCH /actors/1
Content-Type: application/json

{
  "name": "K. Reeves"
}
```

**Update a genre's name:**
```
PATCH /genres/1
Content-Type: application/json

{
  "genre": "Sci-Fi"
}
```

---

## DELETE examples

**Delete a movie:**
```
DELETE /movies/1
```

**Delete an actor:**
```
DELETE /actors/1
```

**Delete a genre:**
```
DELETE /genres/1
```

>Note: use `?force=true` for delete entities with any relationships

---

## Database

- Uses SQLite (`identifier.sqlite`, `mydb.sqlite`)
- Schema is auto-generated on app start
- No external DB setup required

---

## Contributing

You can test endpoints in my Postman workspace:  
[Postman Workspace link](https://app.getpostman.com/join-team?invite_code=483d88ca1ab7f878feb752a283af83b8a2368e812e87341e6623d5f2a377ec49&target_code=433781466108761b2c50000eb0d3d29d)

Use basic auth with:
- username: admin
- password: admin

My Discord: `m4yjunees`
