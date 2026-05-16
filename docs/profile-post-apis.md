# Student, Parent, and Teacher POST APIs

Base URL: `/api/v1`

All endpoints below:

- Require authentication.
- Consume `multipart/form-data`.
- Accept shared user fields through `@ModelAttribute`.
- Accept optional file parts:
  - `profileImage`
  - `profileBackgroundAttachment`
- Return `ApiResponse<T>`.

## Shared Request Fields

These fields are inherited from `UserCreateRequest` and are used by all three POST APIs.

| Field | Type | Required | Validation / Notes |
| --- | --- | --- | --- |
| `firstName` | `string` | Yes | Max 100 characters |
| `lastName` | `string` | Yes | Max 100 characters |
| `middleName` | `string` | No | Max 100 characters |
| `gender` | `Gender` | Yes | `MALE`, `FEMALE` |
| `birthDate` | `LocalDateTime` | No | Example: `2005-04-15T00:00:00` |
| `phoneNumber` | `string` | Yes | Max 30 characters, must match `^[+0-9()\\-\\s]{7,30}$` |
| `email` | `string` | Yes | Valid email, max 255 characters |
| `password` | `string` | Yes | 8 to 255 characters |
| `status` | `UserStatus` | Yes | `ACTIVE`, `INACTIVE`, `BLOCKED`; default is `ACTIVE` |
| `roles` | `List<RoleRequest>` | Yes | At least one role is required |
| `profileImage` | `file` | No | Multipart file part |
| `profileBackgroundAttachment` | `file` | No | Multipart file part |

### RoleRequest

| Field | Type | Required | Notes |
| --- | --- | --- | --- |
| `id` | `UUID` | No | Role id |
| `permissions` | `List<UUID>` | No | Permission ids |

For multipart form data, nested role fields can be sent as:

```text
roles[0].id=<role-uuid>
roles[0].permissions[0]=<permission-uuid>
roles[0].permissions[1]=<permission-uuid>
```

## Shared Response Wrapper

All endpoints return:

```json
{
  "success": true,
  "message": "Profile created successfully",
  "data": {}
}
```

`data` differs by endpoint.

## Base User Response: AuthMe

Student and Parent responses expose base user data as `baseData`. Teacher response exposes it as `user`.

```json
{
  "id": "uuid",
  "firstName": "string",
  "lastName": "string",
  "middleName": "string",
  "email": "string",
  "phoneNumber": "string",
  "gender": "MALE",
  "status": "ACTIVE",
  "birthDate": "string",
  "profileImageAttachmentId": "uuid",
  "profileImageUrl": "string",
  "profileBackgroundAttachmentId": "uuid",
  "profileBackgroundUrl": "string",
  "roles": []
}
```

## Create Student Profile

```http
POST /api/v1/student
Content-Type: multipart/form-data
Authorization: Bearer <access-token>
```

### Request Type

`StudentProfileRequest extends UserCreateRequest`

| Field | Type | Required | Notes |
| --- | --- | --- | --- |
| Shared user fields | See above | See above | Includes `roles` |
| `studentId` | `UUID` | No | Domain-specific student id |
| `studentStatus` | `StudentStatus` | No | `ACTIVE`, `SUSPENDED`, `GRADUATED` |

### cURL Example

```bash
curl -X POST "http://localhost:8888/api/v1/student" \
  -H "Authorization: Bearer <access-token>" \
  -F "firstName=Ali" \
  -F "lastName=Valiyev" \
  -F "gender=MALE" \
  -F "birthDate=2005-04-15T00:00:00" \
  -F "phoneNumber=+998901234567" \
  -F "email=ali.valiyev@example.com" \
  -F "password=Password123" \
  -F "status=ACTIVE" \
  -F "roles[0].id=<role-uuid>" \
  -F "studentId=<student-uuid>" \
  -F "studentStatus=ACTIVE" \
  -F "profileImage=@avatar.png"
```

### Response Type

`ApiResponse<StudentProfileResponse>`

```json
{
  "success": true,
  "message": "Student profile created successfully",
  "data": {
    "baseData": {
      "id": "uuid",
      "firstName": "Ali",
      "lastName": "Valiyev",
      "middleName": null,
      "email": "ali.valiyev@example.com",
      "phoneNumber": "+998901234567",
      "gender": "MALE",
      "status": "ACTIVE",
      "birthDate": "2005-04-15",
      "profileImageAttachmentId": "uuid",
      "profileImageUrl": "http://localhost:8888/api/v1/attachments/uuid",
      "profileBackgroundAttachmentId": null,
      "profileBackgroundUrl": null,
      "roles": []
    },
    "studentId": "uuid",
    "status": "ACTIVE"
  }
}
```

## Create Parent Profile

```http
POST /api/v1/parent
Content-Type: multipart/form-data
Authorization: Bearer <access-token>
```

### Request Type

`ParentProfileRequest extends UserCreateRequest`

| Field | Type | Required | Notes |
| --- | --- | --- | --- |
| Shared user fields | See above | See above | Includes `roles` |
| `studentIds` | `Set<UUID>` | No | Existing student profile ids. If provided, every id must exist. |

For multipart form data, `studentIds` can be sent as indexed fields:

```text
studentIds[0]=<student-profile-uuid>
studentIds[1]=<student-profile-uuid>
```

### cURL Example

```bash
curl -X POST "http://localhost:8888/api/v1/parent" \
  -H "Authorization: Bearer <access-token>" \
  -F "firstName=Karim" \
  -F "lastName=Valiyev" \
  -F "gender=MALE" \
  -F "phoneNumber=+998901112233" \
  -F "email=karim.valiyev@example.com" \
  -F "password=Password123" \
  -F "status=ACTIVE" \
  -F "roles[0].id=<role-uuid>" \
  -F "studentIds[0]=<student-profile-uuid>" \
  -F "profileImage=@avatar.png"
```

### Response Type

`ApiResponse<ParentProfileResponse>`

```json
{
  "success": true,
  "message": "Parent profile created successfully",
  "data": {
    "baseData": {
      "id": "uuid",
      "firstName": "Karim",
      "lastName": "Valiyev",
      "middleName": null,
      "email": "karim.valiyev@example.com",
      "phoneNumber": "+998901112233",
      "gender": "MALE",
      "status": "ACTIVE",
      "birthDate": null,
      "profileImageAttachmentId": "uuid",
      "profileImageUrl": "http://localhost:8888/api/v1/attachments/uuid",
      "profileBackgroundAttachmentId": null,
      "profileBackgroundUrl": null,
      "roles": []
    },
    "studentsCount": 1
  }
}
```

## Create Teacher Profile

```http
POST /api/v1/teacher
Content-Type: multipart/form-data
Authorization: Bearer <access-token>
```

### Request Type

`TeacherProfileRequest extends UserCreateRequest`

| Field | Type | Required | Notes |
| --- | --- | --- | --- |
| Shared user fields | See above | See above | Includes `roles` |
| `position` | `TeacherPosition` | No | `PROFESSOR`, `LECTURER`, `ASSISTANT` |

### cURL Example

```bash
curl -X POST "http://localhost:8888/api/v1/teacher" \
  -H "Authorization: Bearer <access-token>" \
  -F "firstName=Madina" \
  -F "lastName=Karimova" \
  -F "gender=FEMALE" \
  -F "phoneNumber=+998909998877" \
  -F "email=madina.karimova@example.com" \
  -F "password=Password123" \
  -F "status=ACTIVE" \
  -F "roles[0].id=<role-uuid>" \
  -F "position=LECTURER" \
  -F "profileImage=@avatar.png"
```

### Response Type

`ApiResponse<TeacherProfileResponse>`

```json
{
  "success": true,
  "message": "Teacher profile created successfully",
  "data": {
    "teacherId": "uuid",
    "user": {
      "id": "uuid",
      "firstName": "Madina",
      "lastName": "Karimova",
      "middleName": null,
      "email": "madina.karimova@example.com",
      "phoneNumber": "+998909998877",
      "gender": "FEMALE",
      "status": "ACTIVE",
      "birthDate": null,
      "profileImageAttachmentId": "uuid",
      "profileImageUrl": "http://localhost:8888/api/v1/attachments/uuid",
      "profileBackgroundAttachmentId": null,
      "profileBackgroundUrl": null,
      "roles": []
    },
    "position": "LECTURER"
  }
}
```
