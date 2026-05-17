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
  |- application-docker.yml
  |- socket.properties
```

## Configuration

`application.yaml` defines shared defaults, and `application-docker.yml` defines the Docker profile settings.

### Main properties used by this project

- Active profile
  - `spring.profiles.active` (default: `dev`)
- Server
  - `server.port` (default in profile files: `8888`)
- Public app URL
  - `APP_PUBLIC_BASE_URL` (default: `http://localhost:8888`)
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
- Multipart upload limits
  - `SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE` (default: `50MB`)
  - `SPRING_SERVLET_MULTIPART_MAX_REQUEST_SIZE` (default: `100MB`)

## Run Locally With Docker

The Docker setup can run the full backend stack or only selected services.

### 1. Prerequisites

- Docker Desktop or Docker Engine with Docker Compose

### 2. Optional environment file

Create a local `.env` from the template if you want to change ports, database credentials, MinIO credentials, or SMTP values:

```bash
cp .env.example .env
```

On Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

### 3. Run the full stack

```bash
docker compose up -d --build
```

This starts:

- `postgres` on `localhost:5432`
- `minio` API on `localhost:9000`
- `minio` console on `localhost:9001`
- `app` on `localhost:8888`

The `minio-init` one-shot service creates the configured bucket, defaulting to `uploads`.

### 4. Run only selected services

Only MinIO:

```bash
docker compose up -d minio minio-init
```

Only PostgreSQL:

```bash
docker compose up -d postgres
```

PostgreSQL and MinIO without the Java app:

```bash
docker compose up -d postgres minio minio-init
```

Java app with its dependencies:

```bash
docker compose up -d --build app
```

### 5. Stop services

Stop containers while keeping persisted data:

```bash
docker compose down
```

Stop containers and remove Postgres/MinIO volumes:

```bash
docker compose down -v
```

### 6. Service URLs

- App base URL: `http://localhost:8888`
- Swagger UI: `http://localhost:8888/swagger-ui/index.html`
- MinIO console: `http://localhost:9001`
- Default MinIO login: `minioadmin` / `minioadmin`

## Run Locally Without Docker

### 1. Prerequisites

- JDK 21
- Maven (optional if using wrapper)
- PostgreSQL running locally
- Docker (recommended for MinIO)

### 2. Start MinIO via Docker Compose

```bash
docker compose up -d minio minio-init
```

This project includes:

- `minio` on ports `9000` (API) and `9001` (console)
- `minio-init` that creates `uploads` bucket

### 3. Configure database/mail for your environment

Use env-based overrides or local configuration for:

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

## Docker Image Only

Build the Java image without Compose:

```bash
docker build -t lms-backend .
```

Run only the Java container against already-running dependencies:

```bash
docker run --rm -p 8888:8888 --name lms-backend \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/lms \
  -e SPRING_DATASOURCE_USERNAME=lms \
  -e SPRING_DATASOURCE_PASSWORD=lms_password \
  -e MINIO_ENDPOINT=http://host.docker.internal:9000 \
  lms-backend
```

On Windows PowerShell, use backticks instead of backslashes for line continuation.

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
  - `GET /api/v1/attachments/{id}`
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
