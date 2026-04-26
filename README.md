# LMS Backend

Spring Boot 4 backend for an LMS platform with JWT authentication, role/permission management, user profiles, LMS domain services, file upload support, and OpenAPI docs.

## Tech Stack

- Java 21
- Spring Boot 4.0.3
- Spring Web, Spring Security, Spring Data JPA
- PostgreSQL
- MinIO (object storage)
- TUS resumable upload protocol
- JWT (`jjwt`)
- MapStruct + Lombok
- Caffeine cache
- SpringDoc OpenAPI (Swagger UI)
- Maven Wrapper (`mvnw`, `mvnw.cmd`)

## Current Feature Scope

- Authentication and authorization
  - JWT-based auth
  - Custom open endpoint annotation: `@OpenAuth`
  - Role and permission domain (services + repositories)
- User and profile modules
  - Users
  - Teacher profile
  - Student profile
  - Parent profile
- File handling
  - Multipart attachment upload/delete endpoints
  - MinIO-backed storage service
  - TUS resumable upload endpoint (`/files/**`)
- Realtime
  - STOMP/SockJS endpoint on `/ws`
- API docs
  - Swagger UI enabled via SpringDoc

## Project Structure

```text
src/main/java/uz/mirmaxsudov/lmsbackend
  |- controller/            # REST controllers
  |- service/               # business logic
  |- repository/            # JPA repositories + specifications
  |- model/                 # entities, requests, responses, enums
  |- security/              # JWT filter, auth handlers, user details
  |- config/                # security, minio, hibernate, rate-limit, swagger
  |- tus/                   # TUS protocol helpers/store
  |- storage/               # storage abstraction
  |- util/common            # shared helpers

src/main/resources
  |- application.yaml
  |- application-dev.yml
  |- application-prod.yml
  |- jwt.properties
  |- socket.properties
```

## Configuration

`application.yaml` defines shared defaults, and `application-dev.yml` / `application-prod.yml` define environment-specific settings.

### Main properties used by this project

- Active profile
  - `spring.profiles.active` (default: `dev`)
- Server
  - `server.port` (default in profile files: `8888`)
- PostgreSQL
  - `spring.datasource.url`
  - `spring.datasource.username`
  - `spring.datasource.password`
- MinIO
  - `MINIO_ENDPOINT` (default: `http://localhost:9000`)
  - `MINIO_ACCESS_KEY` (default: `minioadmin`)
  - `MINIO_SECRET_KEY` (default: `minioadmin`)
  - `MINIO_BUCKET` (default: `uploads`)
  - `MINIO_REGION` (default: `us-east-1`)
- TUS
  - `TUS_MAX_UPLOAD_SIZE_BYTES` (default: `10737418240` = 10 GiB)
  - `TUS_CHUNK_CLEANUP_ON_COMPLETE` (default: `true`)

## Run Locally

### 1. Prerequisites

- JDK 21
- Maven (optional if using wrapper)
- PostgreSQL running locally
- Docker (recommended for MinIO)

### 2. Start MinIO via Docker Compose

```bash
docker compose up -d
```

This project includes:

- `minio` on ports `9000` (API) and `9001` (console)
- `minio-init` that creates `uploads` bucket

### 3. Configure database/mail for your environment

Update `src/main/resources/application-dev.yml` (or use env-based overrides) for:

- PostgreSQL connection
- SMTP credentials

### 4. Start the app

Windows:

```bash
mvnw.cmd spring-boot:run
```

macOS/Linux:

```bash
./mvnw spring-boot:run
```

App base URL: `http://localhost:8888`

## Build and Test

Build:

```bash
mvnw.cmd clean package
```

Run tests:

```bash
mvnw.cmd test
```

## Docker

Build image:

```bash
docker build -t lms-backend .
```

Run container:

```bash
docker run --rm -p 8888:8888 --name lms-backend lms-backend
```

Note: when running in Docker, ensure DB/MinIO endpoints are reachable from the container and passed via environment variables.

## API Documentation

After app startup:

- Swagger UI: `http://localhost:8888/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8888/v3/api-docs`

## Public and Core Endpoints (Current Controllers)

Base API prefix for REST modules: `/api/v1`

- Auth
  - `POST /api/v1/auth/login` (`@OpenAuth`)
  - `POST /api/v1/auth/logout` (`@OpenAuth`)
  - `GET /api/v1/auth/me`
  - `PATCH /api/v1/auth/me` (JSON or multipart)
- Users / Profiles
  - `/api/v1/user`
  - `/api/v1/teacher`
  - `/api/v1/student`
  - `/api/v1/parent`
- LMS Core
  - `/api/v1/courses`
  - `/api/v1/groups`
  - `/api/v1/course-sections`
  - `/api/v1/lessons`
- Attachments
  - `POST /api/v1/attachments`
  - `POST /api/v1/attachments/bulk`
  - `DELETE /api/v1/attachments/{id}`
- TUS uploads (`@OpenAuth` at controller level)
  - `OPTIONS /files` and `/files/{id}`
  - `POST /files`
  - `PATCH /files/{id}`
  - `HEAD /files/{id}`
  - `DELETE /files/{id}`
  - `GET /files/{id}`

## WebSocket

- STOMP endpoint: `/ws` (SockJS enabled)
- Broker destination prefix: `/topic`
- Application destination prefix: `/app`

## Notes

- Security whitelist includes Swagger and WebSocket endpoints.
- `DataInitializer` and `UserInitializer` classes are currently commented out.
- Before production use, move sensitive values (DB, SMTP, JWT secrets) out of repository files and inject via environment/secrets manager.

## Sample Requests

### Course Section

#### Create Section
**POST** `/api/v1/course-sections`
```json
{
  "title": "Module 1: Getting Started",
  "courseId": "550e8400-e29b-41d4-a716-446655440000",
  "orderIndex": 1
}
```

### Lesson

#### Create Lesson
**POST** `/api/v1/lessons`
```json
{
  "title": "Setting Up Environment",
  "sectionId": "660f9500-f30c-52e5-b827-557766551111",
  "content": "Step-by-step guide...",
  "durationInMinutes": 30
}
```
