# Online Course API Guide for Frontend Engineers

This document explains how the frontend should integrate with the Online Course module.

Base API URL:

```text
/api/v1/online-courses
```

All endpoints require JWT authentication unless stated otherwise. Send the token as:

```http
Authorization: Bearer <accessToken>
```

## Roles

The API supports these system roles:

- `SUPER_ADMIN`
- `ADMIN`
- `MAINTAINER`
- `TEACHER`
- `SUPPORT_TEACHER`
- `STUDENT`
- `PARENT`
- `GUARDIAN`

Access rules:

- `SUPER_ADMIN`, `ADMIN`, `MAINTAINER`: can manage all online courses and enrollments.
- `TEACHER`, `SUPPORT_TEACHER`: can create and manage only their own online courses and content.
- `STUDENT`: can browse published courses, enroll self, view own enrollments, and update own progress.
- `PARENT`, `GUARDIAN`: can view enrollments/progress for linked students only.

## Enums

### CourseLevel

```text
BEGINNER
INTERMEDIATE
ADVANCED
```

### OnlineCourseStatus

```text
DRAFT
PUBLISHED
ARCHIVED
```

### OnlineCourseContentStatus

```text
DRAFT
PUBLISHED
HIDDEN
```

### OnlineCourseUnlockStrategy

```text
ALL_AT_ONCE
MODULE_BY_MODULE
LESSON_BY_LESSON
```

### OnlineCourseEnrollmentStatus

```text
ACTIVE
COMPLETED
SUSPENDED
CANCELLED
```

### OnlineCourseProgressStatus

```text
LOCKED
AVAILABLE
IN_PROGRESS
COMPLETED
```

Frontend must not send `LOCKED` when updating lesson progress.

## Course APIs

### Get Online Courses

```http
GET /api/v1/online-courses
```

Query params:

| Name | Type | Required | Notes |
|---|---:|---:|---|
| `page` | number | no | Default `1` |
| `size` | number | no | Default `10` |
| `search` | string | no | Searches title, slug, short description, description |
| `level` | enum | no | `BEGINNER`, `INTERMEDIATE`, `ADVANCED` |
| `status` | enum | no | Managers/authors can filter by status |
| `createdById` | UUID | no | Managers/authors only |
| `minDuration` | number | no | Must be greater than `0` |
| `maxDuration` | number | no | Must be greater than `0` |

Student, parent, and guardian users only receive `PUBLISHED` courses.

Example:

```http
GET /api/v1/online-courses?page=1&size=12&level=BEGINNER&search=java
```

Response shape:

```json
{
  "success": true,
  "message": "Online courses fetched successfully",
  "results": [
    {
      "id": "3fb74f99-9792-4e66-85fb-f8db6b066d69",
      "title": "Java 21 Backend Foundations",
      "slug": "java-21-backend-foundations",
      "shortDescription": "Build a solid Java backend base...",
      "level": "BEGINNER",
      "status": "PUBLISHED",
      "unlockStrategy": "LESSON_BY_LESSON",
      "estimatedDurationInMinutes": 263,
      "thumbnailId": null,
      "thumbnailUrl": null
    }
  ],
  "total": 10,
  "page": 1,
  "size": 12,
  "hasNext": false
}
```

### Get Course Detail

```http
GET /api/v1/online-courses/{courseId}
```

Response includes modules, lessons, and lesson materials.

Non-owner students/parents/guardians only see published content. Managers and the course author can see draft/hidden content too.

### Create Course

Allowed roles: `SUPER_ADMIN`, `ADMIN`, `MAINTAINER`, `TEACHER`, `SUPPORT_TEACHER`.

```http
POST /api/v1/online-courses
Content-Type: application/json
```

Request:

```json
{
  "title": "Spring Boot REST API Mastery",
  "slug": "spring-boot-rest-api-mastery",
  "shortDescription": "Design predictable REST APIs with Spring Boot.",
  "description": "Full course description.",
  "level": "INTERMEDIATE",
  "status": "DRAFT",
  "unlockStrategy": "LESSON_BY_LESSON",
  "estimatedDurationInMinutes": 240,
  "thumbnailId": null
}
```

Notes:

- `slug` is optional. If omitted, backend generates it from `title`.
- `status` defaults to `DRAFT`.
- `unlockStrategy` defaults to `LESSON_BY_LESSON`.
- `thumbnailId` must be an existing attachment id if provided.

### Update Course

```http
PUT /api/v1/online-courses/{courseId}
Content-Type: application/json
```

Same payload as create, but `status` and `unlockStrategy` are required.

### Delete Course

```http
DELETE /api/v1/online-courses/{courseId}
```

This is a soft delete.

## Module APIs

### Create Module

```http
POST /api/v1/online-courses/{courseId}/modules
Content-Type: application/json
```

Request:

```json
{
  "title": "Getting Started",
  "description": "Introductory module.",
  "orderIndex": 0,
  "status": "PUBLISHED",
  "availableFrom": null
}
```

Notes:

- `orderIndex` must be unique per course.
- `status` defaults to `DRAFT`.
- `availableFrom` is optional ISO datetime, for example `2026-05-09T10:00:00`.

### Update Module

```http
PUT /api/v1/online-courses/modules/{moduleId}
```

### Delete Module

```http
DELETE /api/v1/online-courses/modules/{moduleId}
```

This is a soft delete.

## Lesson APIs

### Create Lesson

```http
POST /api/v1/online-courses/modules/{moduleId}/lessons
Content-Type: application/json
```

Request:

```json
{
  "title": "Introduction to Java Streams",
  "description": "Overview of stream pipelines.",
  "content": "Lesson notes or rich text content.",
  "orderIndex": 0,
  "durationInMinutes": 25,
  "freePreview": true,
  "status": "PUBLISHED",
  "availableFrom": null,
  "videoAttachmentId": null
}
```

Notes:

- `orderIndex` must be unique per module.
- `status` defaults to `DRAFT`.
- `videoAttachmentId` is optional.
- If `videoAttachmentId` is provided, it must be an existing attachment id.

### Update Lesson

```http
PUT /api/v1/online-courses/lessons/{lessonId}
Content-Type: application/json
```

Same payload as create, but `status` is required.

### Delete Lesson

```http
DELETE /api/v1/online-courses/lessons/{lessonId}
```

This is a soft delete.

## Lesson Material APIs

Materials are regular attachments linked to a lesson.

### Add Lesson Material

```http
POST /api/v1/online-courses/lessons/{lessonId}/materials
Content-Type: application/json
```

Request:

```json
{
  "attachmentId": "d0afabcf-cf40-40f0-b29d-f3dfd929d4d8",
  "title": "Lesson PDF",
  "orderIndex": 0
}
```

### Update Lesson Material

```http
PUT /api/v1/online-courses/materials/{materialId}
```

### Delete Lesson Material

```http
DELETE /api/v1/online-courses/materials/{materialId}
```

This is a soft delete.

## Enrollment APIs

### Student Self-Enroll

Allowed role: `STUDENT`.

```http
POST /api/v1/online-courses/{courseId}/enrollments/me
```

Rules:

- Course must be `PUBLISHED`.
- Current user must have a student profile.
- Duplicate active enrollment is rejected.
- Backend creates initial module/lesson progress rows based on `unlockStrategy`.

### Admin Creates Enrollment

Allowed roles: `SUPER_ADMIN`, `ADMIN`, `MAINTAINER`.

```http
POST /api/v1/online-courses/{courseId}/enrollments
Content-Type: application/json
```

Request:

```json
{
  "studentProfileId": "a4ee35de-bcdd-443b-a18c-e176f3d2f062",
  "status": "ACTIVE"
}
```

`status` is optional and defaults to `ACTIVE`.

### Get My Enrollments

Allowed role: `STUDENT`.

```http
GET /api/v1/online-courses/enrollments/me?page=1&size=10&status=ACTIVE
```

### Get All Enrollments

Allowed roles: `SUPER_ADMIN`, `ADMIN`, `MAINTAINER`.

```http
GET /api/v1/online-courses/enrollments
```

Query params:

| Name | Type | Required |
|---|---:|---:|
| `page` | number | no |
| `size` | number | no |
| `courseId` | UUID | no |
| `studentProfileId` | UUID | no |
| `status` | enum | no |

### Get Student Enrollments

Allowed roles: `SUPER_ADMIN`, `ADMIN`, `MAINTAINER`, `STUDENT`, `PARENT`, `GUARDIAN`.

```http
GET /api/v1/online-courses/students/{studentProfileId}/enrollments
```

Rules:

- Managers can view any student.
- Students can view only their own student profile.
- Parents/guardians can view only linked student profiles.

## Progress API

### Update Lesson Progress

Allowed role: `STUDENT`.

```http
PATCH /api/v1/online-courses/lessons/{lessonId}/progress
Content-Type: application/json
```

Request:

```json
{
  "status": "IN_PROGRESS",
  "lastPositionInSeconds": 124
}
```

To complete a lesson:

```json
{
  "status": "COMPLETED",
  "lastPositionInSeconds": 1500
}
```

Rules:

- Current user must be enrolled in the lesson's course.
- Enrollment must be `ACTIVE`.
- Lesson progress must not be `LOCKED`.
- Frontend must not send `LOCKED`.
- Backend updates module progress, unlocks next content, and completes enrollment when all lessons are completed.

## Enrollment Response Shape

Enrollment responses include course summary, student info, current module/lesson, progress counts, and progress rows.

```json
{
  "id": "74b98557-0688-4024-bbe4-8ecf79cb27d1",
  "course": {
    "id": "3fb74f99-9792-4e66-85fb-f8db6b066d69",
    "title": "Java 21 Backend Foundations",
    "slug": "java-21-backend-foundations",
    "level": "BEGINNER",
    "status": "PUBLISHED",
    "unlockStrategy": "LESSON_BY_LESSON",
    "estimatedDurationInMinutes": 263,
    "thumbnailId": null,
    "thumbnailUrl": null
  },
  "studentProfileId": "a4ee35de-bcdd-443b-a18c-e176f3d2f062",
  "studentUserId": "99e4967b-63d4-40a7-9200-20244e29bf3b",
  "studentName": "Ali Valiyev",
  "openedById": "99e4967b-63d4-40a7-9200-20244e29bf3b",
  "status": "ACTIVE",
  "openedAt": "2026-05-09T14:00:00",
  "completedAt": null,
  "currentModuleId": "9fabfbc9-1faa-4e4a-86cf-db6fdb86e257",
  "currentLessonId": "320160ce-94a6-4c28-999b-d3bf59378e4f",
  "completedLessons": 1,
  "totalLessons": 9,
  "progressPercentage": 11.11111111111111,
  "moduleProgresses": [],
  "lessonProgresses": []
}
```

## Video Upload Status

`OnlineCourseLesson` supports video through:

```json
{
  "videoAttachmentId": "attachment-uuid"
}
```

Current important limitation:

- The regular attachment API creates `Attachment` records.
- The TUS upload API currently creates a resumable upload object under `/files/{uploadId}`.
- TUS upload id is not the same as `Attachment.id`.

Frontend cannot directly pass a TUS upload id as `videoAttachmentId`.

Recommended backend addition:

```http
POST /api/v1/attachments/tus/{uploadId}/complete
```

That endpoint should create an `Attachment` row for the completed TUS object and return the new `Attachment.id`. Then frontend can pass that id as `videoAttachmentId`.

## Frontend Implementation Checklist

- Use course list for catalog pages.
- Use course detail for syllabus/module/lesson tree.
- Hide draft/hidden editing controls unless user is manager or course author.
- Use `POST /enrollments/me` for student enrollment.
- Use enrollment response to render locked, available, in-progress, and completed lessons.
- Disable progress updates for locked lessons.
- Send `IN_PROGRESS` while watching video and `COMPLETED` when lesson is finished.
- Use regular attachment ids for lesson videos until TUS-to-attachment finalization exists.
