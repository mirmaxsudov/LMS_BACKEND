# Role and Permission API Guide

Base URL: `/api/v1`

All endpoints below:

- Require authentication.
- Consume and return `application/json`, except `GET` and `DELETE` requests without a body.
- Return `ApiResponse<T>` for single-resource actions.
- Return `ApiPaginateResponse<T>` for list actions.

Access rules:

| Action | Required Authority |
| --- | --- |
| View roles or permissions | `SUPER_ADMIN` role or `PERM_USER_VIEW` authority |
| Create or update roles or permissions | `SUPER_ADMIN` role or `PERM_USER_EDIT` authority |
| Delete roles or permissions | `SUPER_ADMIN` role or `PERM_USER_DELETE` authority |

## Shared Response Wrappers

### ApiResponse

| Field | Type | Notes |
| --- | --- | --- |
| `success` | `boolean` | Request result |
| `message` | `string` | Human-readable message |
| `data` | `T` | Response payload. For delete responses this is `null` or omitted depending on JSON configuration. |

Example:

```json
{
  "success": true,
  "message": "Role fetched successfully",
  "data": {}
}
```

### ApiPaginateResponse

| Field | Type | Notes |
| --- | --- | --- |
| `success` | `boolean` | Request result |
| `message` | `string` | Human-readable message |
| `results` | `T` | Paginated result list |
| `total` | `number` | Total matching records |
| `page` | `number` | Current 1-based page |
| `size` | `number` | Page size used by backend |
| `hasNext` | `boolean` | Whether another page exists |

Example:

```json
{
  "success": true,
  "message": "Roles fetched successfully",
  "results": [],
  "total": 0,
  "page": 1,
  "size": 10,
  "hasNext": false
}
```

## Enums

### PermissionCategory

```text
SYSTEM
USER
COURSE
GROUP
LESSON
ATTENDANCE
ENROLLMENT
```

## Role Types

### RoleCreateRequest

Used by `POST /api/v1/roles`.

| Field | Type | Required | Validation / Notes |
| --- | --- | --- | --- |
| `name` | `string` | Yes | Not blank, max 100 characters. Backend trims, replaces spaces and hyphens with `_`, and stores uppercase. |
| `description` | `string` | No | Max 1000 characters. Blank values are stored as `null`. |
| `permissionIds` | `Set<UUID>` | No | Defaults to an empty set. Every non-empty id must point to an active permission. `null` ids are rejected. |

Example:

```json
{
  "name": "course manager",
  "description": "Can manage course content",
  "permissionIds": [
    "5c19c8ef-7f9a-4c52-b236-42d622de13b5",
    "8c750025-97f8-42c0-8e4d-6df50412ad4f"
  ]
}
```

### RoleUpdateRequest

Used by `PUT /api/v1/roles/{id}`.

Same fields and validation as `RoleCreateRequest`.

Notes:

- `id` is sent in the path, not in the JSON body.
- Updating a reserved system role name is rejected if the normalized name changes.
- The submitted `permissionIds` replaces the role's existing permissions.

### AuthMeRole Response

| Field | Type | Notes |
| --- | --- | --- |
| `id` | `UUID` | Role id |
| `createdAt` | `LocalDateTime` | Example: `2026-05-31T14:30:00` |
| `updatedAt` | `LocalDateTime` | Example: `2026-05-31T14:45:00` |
| `name` | `string` | Normalized uppercase role name |
| `description` | `string \| null` | Role description |
| `permissions` | `Set<AuthMePermission>` | Active permissions assigned to the role |

Example:

```json
{
  "id": "4e7dc4c9-8f74-4705-a13e-2c5d8e836e67",
  "createdAt": "2026-05-31T14:30:00",
  "updatedAt": "2026-05-31T14:45:00",
  "name": "COURSE_MANAGER",
  "description": "Can manage course content",
  "permissions": [
    {
      "id": "5c19c8ef-7f9a-4c52-b236-42d622de13b5",
      "createdAt": "2026-05-31T14:00:00",
      "updatedAt": "2026-05-31T14:00:00",
      "code": "PERM_COURSE_VIEW",
      "description": "Can view courses",
      "module": "course",
      "action": "view",
      "category": "COURSE",
      "isSystem": false
    }
  ]
}
```

## Permission Types

### PermissionCreateRequest

Used by `POST /api/v1/permissions`.

| Field | Type | Required | Validation / Notes |
| --- | --- | --- | --- |
| `code` | `string` | Yes | Not blank, max 150 characters. Backend trims, replaces spaces and hyphens with `_`, and stores uppercase. Must be unique among active permissions. |
| `description` | `string` | No | Max 1000 characters. Blank values are stored as `null`. |
| `module` | `string` | No | Max 100 characters. Blank values are stored as `null`. |
| `action` | `string` | No | Max 100 characters. Blank values are stored as `null`. |
| `category` | `PermissionCategory` | Yes | One of the enum values listed above. |
| `isSystem` | `boolean` | No | Defaults to `false`. |

Example:

```json
{
  "code": "perm course publish",
  "description": "Can publish online courses",
  "module": "course",
  "action": "publish",
  "category": "COURSE",
  "isSystem": false
}
```

### PermissionUpdateRequest

Used by `PUT /api/v1/permissions/{id}`.

Same fields and validation as `PermissionCreateRequest`.

Notes:

- `id` is sent in the path, not in the JSON body.
- Updating a system permission code is rejected if the normalized code changes.
- Once a permission is system-level, update requests cannot turn `isSystem` back to `false`.

### AuthMePermission Response

| Field | Type | Notes |
| --- | --- | --- |
| `id` | `UUID` | Permission id |
| `createdAt` | `LocalDateTime` | Example: `2026-05-31T14:00:00` |
| `updatedAt` | `LocalDateTime` | Example: `2026-05-31T14:15:00` |
| `code` | `string` | Normalized uppercase permission code |
| `description` | `string \| null` | Permission description |
| `module` | `string \| null` | Permission module |
| `action` | `string \| null` | Permission action |
| `category` | `PermissionCategory` | Permission category |
| `isSystem` | `boolean` | Whether the permission is protected as a system permission |

Example:

```json
{
  "id": "5c19c8ef-7f9a-4c52-b236-42d622de13b5",
  "createdAt": "2026-05-31T14:00:00",
  "updatedAt": "2026-05-31T14:15:00",
  "code": "PERM_COURSE_PUBLISH",
  "description": "Can publish online courses",
  "module": "course",
  "action": "publish",
  "category": "COURSE",
  "isSystem": false
}
```

## Role Endpoints

### Get Roles

```http
GET /api/v1/roles
Authorization: Bearer <access-token>
```

Query params:

| Name | Type | Required | Default | Notes |
| --- | --- | --- | --- | --- |
| `page` | `number` | No | `1` | 1-based page number. Values below `1` are normalized to page `1`. |
| `size` | `number` | No | `10` | Values below `1` are normalized to `10`. |
| `search` | `string` | No | - | Searches role `name` and `description`. |
| `permissionId` | `UUID` | No | - | Filters roles assigned to this permission id. |

Response type: `ApiPaginateResponse<List<AuthMeRole>>`

Example:

```http
GET /api/v1/roles?page=1&size=10&search=course&permissionId=5c19c8ef-7f9a-4c52-b236-42d622de13b5
```

```json
{
  "success": true,
  "message": "Roles fetched successfully",
  "results": [
    {
      "id": "4e7dc4c9-8f74-4705-a13e-2c5d8e836e67",
      "createdAt": "2026-05-31T14:30:00",
      "updatedAt": "2026-05-31T14:45:00",
      "name": "COURSE_MANAGER",
      "description": "Can manage course content",
      "permissions": []
    }
  ],
  "total": 1,
  "page": 1,
  "size": 10,
  "hasNext": false
}
```

### Get Role By Id

```http
GET /api/v1/roles/{id}
Authorization: Bearer <access-token>
```

Path params:

| Name | Type | Required | Notes |
| --- | --- | --- | --- |
| `id` | `UUID` | Yes | Active role id |

Response type: `ApiResponse<AuthMeRole>`

### Create Role

```http
POST /api/v1/roles
Authorization: Bearer <access-token>
Content-Type: application/json
```

Request type: `RoleCreateRequest`

Response type: `ApiResponse<AuthMeRole>`

Success message: `Role created successfully`

### Update Role

```http
PUT /api/v1/roles/{id}
Authorization: Bearer <access-token>
Content-Type: application/json
```

Path params:

| Name | Type | Required | Notes |
| --- | --- | --- | --- |
| `id` | `UUID` | Yes | Active role id |

Request type: `RoleUpdateRequest`

Response type: `ApiResponse<AuthMeRole>`

Success message: `Role updated successfully`

### Delete Role

```http
DELETE /api/v1/roles/{id}
Authorization: Bearer <access-token>
```

Path params:

| Name | Type | Required | Notes |
| --- | --- | --- | --- |
| `id` | `UUID` | Yes | Active non-system role id |

Response type: `ApiResponse<Void>`

Success message: `Role deleted successfully`

Notes:

- Reserved system roles cannot be deleted.
- Roles assigned to at least one active user cannot be deleted.
- Delete is a soft delete.

## Permission Endpoints

### Get Permissions

```http
GET /api/v1/permissions
Authorization: Bearer <access-token>
```

Query params:

| Name | Type | Required | Default | Notes |
| --- | --- | --- | --- | --- |
| `page` | `number` | No | `1` | 1-based page number. Values below `1` are normalized to page `1`. |
| `size` | `number` | No | `10` | Values below `1` are normalized to `10`. |
| `search` | `string` | No | - | Searches `code`, `description`, `module`, and `action`. |
| `module` | `string` | No | - | Exact case-insensitive match after trimming. |
| `action` | `string` | No | - | Exact case-insensitive match after trimming. |
| `category` | `PermissionCategory` | No | - | One of the enum values listed above. |
| `isSystem` | `boolean` | No | - | Filters system or non-system permissions. |

Response type: `ApiPaginateResponse<List<AuthMePermission>>`

Example:

```http
GET /api/v1/permissions?page=1&size=10&module=course&action=view&category=COURSE&isSystem=false
```

```json
{
  "success": true,
  "message": "Permissions fetched successfully",
  "results": [
    {
      "id": "5c19c8ef-7f9a-4c52-b236-42d622de13b5",
      "createdAt": "2026-05-31T14:00:00",
      "updatedAt": "2026-05-31T14:15:00",
      "code": "PERM_COURSE_VIEW",
      "description": "Can view courses",
      "module": "course",
      "action": "view",
      "category": "COURSE",
      "isSystem": false
    }
  ],
  "total": 1,
  "page": 1,
  "size": 10,
  "hasNext": false
}
```

### Get Permission By Id

```http
GET /api/v1/permissions/{id}
Authorization: Bearer <access-token>
```

Path params:

| Name | Type | Required | Notes |
| --- | --- | --- | --- |
| `id` | `UUID` | Yes | Active permission id |

Response type: `ApiResponse<AuthMePermission>`

### Create Permission

```http
POST /api/v1/permissions
Authorization: Bearer <access-token>
Content-Type: application/json
```

Request type: `PermissionCreateRequest`

Response type: `ApiResponse<AuthMePermission>`

Success message: `Permission created successfully`

### Update Permission

```http
PUT /api/v1/permissions/{id}
Authorization: Bearer <access-token>
Content-Type: application/json
```

Path params:

| Name | Type | Required | Notes |
| --- | --- | --- | --- |
| `id` | `UUID` | Yes | Active permission id |

Request type: `PermissionUpdateRequest`

Response type: `ApiResponse<AuthMePermission>`

Success message: `Permission updated successfully`

### Delete Permission

```http
DELETE /api/v1/permissions/{id}
Authorization: Bearer <access-token>
```

Path params:

| Name | Type | Required | Notes |
| --- | --- | --- | --- |
| `id` | `UUID` | Yes | Active non-system permission id |

Response type: `ApiResponse<Void>`

Success message: `Permission deleted successfully`

Notes:

- System permissions cannot be deleted.
- Permissions assigned to at least one active role cannot be deleted.
- Delete is a soft delete.
