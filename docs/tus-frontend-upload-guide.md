# TUS Resumable Upload Guide for Frontend Engineers

This document explains how to upload, resume, delete, and download files using the backend TUS upload endpoint.

TUS endpoint root:

```text
/files
```

Current backend behavior:

- TUS version: `1.0.0`
- Supported extensions: `creation`, `termination`
- Default max upload size: `10 GiB`
- Uploads are stored in MinIO under `uploads/{uploadId}`
- Completed uploads can be downloaded from `GET /files/{uploadId}`
- `/files/**` is currently annotated with `@OpenAuth`, so it is public in this backend configuration
- TUS upload id is **not** an `Attachment.id`

## Required Headers

Every TUS protocol request must include:

```http
Tus-Resumable: 1.0.0
```

For chunk uploads:

```http
Content-Type: application/offset+octet-stream
Upload-Offset: <current-offset>
Content-Length: <chunk-size>
```

## Check Server Capabilities

Use `OPTIONS /files` before upload if the frontend needs max size or supported extensions.

```http
OPTIONS /files
Tus-Resumable: 1.0.0
```

Success response:

```http
204 No Content
Tus-Resumable: 1.0.0
Tus-Version: 1.0.0
Tus-Extension: creation,termination
Tus-Max-Size: 10737418240
```

`Tus-Max-Size` is bytes.

## Create Upload

Create an upload resource before sending chunks.

```http
POST /files
Tus-Resumable: 1.0.0
Upload-Length: 524288000
Upload-Metadata: filename bGVzc29uLTEubXA0,contentType dmlkZW8vbXA0
```

Response:

```http
201 Created
Location: http://localhost:8888/files/8fa6b87b-4e79-48b5-9c40-cc9679c05f62
Upload-Offset: 0
Upload-Length: 524288000
Tus-Resumable: 1.0.0
Tus-Version: 1.0.0
```

Store the returned `Location` and the last path segment as `uploadId`.

Example:

```text
Location: http://localhost:8888/files/8fa6b87b-4e79-48b5-9c40-cc9679c05f62
uploadId: 8fa6b87b-4e79-48b5-9c40-cc9679c05f62
```

## Upload Metadata

`Upload-Metadata` format:

```text
key base64(value),key2 base64(value2)
```

Recommended metadata keys:

```text
filename
contentType
```

JavaScript helper:

```ts
function encodeTusMetadata(metadata: Record<string, string>) {
  return Object.entries(metadata)
    .map(([key, value]) => `${key} ${btoa(unescape(encodeURIComponent(value)))}`)
    .join(",");
}

const uploadMetadata = encodeTusMetadata({
  filename: file.name,
  contentType: file.type || "application/octet-stream",
});
```

The backend uses metadata during download:

- `filename` becomes the download filename.
- `contentType` becomes the response `Content-Type`.

## Upload Chunks

Send file chunks with `PATCH`.

```http
PATCH /files/{uploadId}
Tus-Resumable: 1.0.0
Upload-Offset: 0
Content-Type: application/offset+octet-stream
Content-Length: 10485760

<binary chunk bytes>
```

Success response:

```http
204 No Content
Tus-Resumable: 1.0.0
Tus-Version: 1.0.0
Upload-Offset: 10485760
```

Use the response `Upload-Offset` as the next chunk offset.

When the offset equals `Upload-Length`, the backend automatically composes chunks into the final object and marks the upload completed.

## Resume Upload

If upload is interrupted, ask the backend for the current offset.

```http
HEAD /files/{uploadId}
Tus-Resumable: 1.0.0
```

Success response:

```http
200 OK
Tus-Resumable: 1.0.0
Tus-Version: 1.0.0
Upload-Offset: 20971520
Upload-Length: 524288000
Cache-Control: no-store
```

Continue `PATCH` from `Upload-Offset`.

## Delete Upload

Use this to cancel an unfinished upload or remove a completed upload object.

```http
DELETE /files/{uploadId}
Tus-Resumable: 1.0.0
```

Success response:

```http
204 No Content
Tus-Resumable: 1.0.0
Tus-Version: 1.0.0
```

## Download Completed Upload

Download or stream a completed upload:

```http
GET /files/{uploadId}
```

Full response:

```http
200 OK
Accept-Ranges: bytes
Content-Type: video/mp4
Content-Length: 524288000
Content-Disposition: attachment; filename*=UTF-8''lesson-1.mp4
```

For video playback, the browser may send range requests:

```http
GET /files/{uploadId}
Range: bytes=0-1048575
```

Partial response:

```http
206 Partial Content
Accept-Ranges: bytes
Content-Range: bytes 0-1048575/524288000
Content-Type: video/mp4
Content-Length: 1048576
```

## Browser Upload Example

This example uses `fetch` and manual chunking.

```ts
const TUS_VERSION = "1.0.0";
const CHUNK_SIZE = 10 * 1024 * 1024;

function encodeTusMetadata(metadata: Record<string, string>) {
  return Object.entries(metadata)
    .map(([key, value]) => `${key} ${btoa(unescape(encodeURIComponent(value)))}`)
    .join(",");
}

async function createTusUpload(file: File) {
  const response = await fetch("/files", {
    method: "POST",
    headers: {
      "Tus-Resumable": TUS_VERSION,
      "Upload-Length": String(file.size),
      "Upload-Metadata": encodeTusMetadata({
        filename: file.name,
        contentType: file.type || "application/octet-stream",
      }),
    },
  });

  if (!response.ok) {
    throw new Error(`Create upload failed: ${response.status}`);
  }

  const location = response.headers.get("Location");
  if (!location) {
    throw new Error("Upload Location header is missing");
  }

  return location;
}

async function getTusOffset(uploadUrl: string) {
  const response = await fetch(uploadUrl, {
    method: "HEAD",
    headers: {
      "Tus-Resumable": TUS_VERSION,
    },
  });

  if (!response.ok) {
    throw new Error(`Get upload offset failed: ${response.status}`);
  }

  return Number(response.headers.get("Upload-Offset") || 0);
}

async function uploadFile(file: File, onProgress?: (percent: number) => void) {
  const uploadUrl = await createTusUpload(file);
  let offset = await getTusOffset(uploadUrl);

  while (offset < file.size) {
    const chunk = file.slice(offset, Math.min(offset + CHUNK_SIZE, file.size));

    const response = await fetch(uploadUrl, {
      method: "PATCH",
      headers: {
        "Tus-Resumable": TUS_VERSION,
        "Upload-Offset": String(offset),
        "Content-Type": "application/offset+octet-stream",
      },
      body: chunk,
    });

    if (!response.ok) {
      throw new Error(`Upload chunk failed: ${response.status}`);
    }

    offset = Number(response.headers.get("Upload-Offset") || offset + chunk.size);
    onProgress?.((offset / file.size) * 100);
  }

  return {
    uploadUrl,
    uploadId: uploadUrl.substring(uploadUrl.lastIndexOf("/") + 1),
    downloadUrl: `/files/${uploadUrl.substring(uploadUrl.lastIndexOf("/") + 1)}`,
  };
}
```

Usage:

```ts
const result = await uploadFile(file, (percent) => {
  console.log(`Uploaded ${percent.toFixed(1)}%`);
});

console.log(result.uploadId);
console.log(result.downloadUrl);
```

## Error Responses

TUS errors return JSON:

```json
{
  "timestamp": "2026-05-10T10:00:00Z",
  "status": 409,
  "error": "Conflict",
  "message": "Invalid Upload-Offset. Expected 10485760 but received 0"
}
```

Common errors:

| Status | Cause |
|---:|---|
| `400` | Missing `Upload-Length`, invalid range, invalid metadata base64, chunk exceeds length |
| `404` | Upload id not found |
| `409` | Wrong upload offset, upload already completed, download before completion |
| `412` | Missing or unsupported `Tus-Resumable` header |
| `413` | Upload is larger than max upload size |
| `416` | Invalid byte range for download |
| `500` | Storage/MinIO failure |

## Important Limitation: TUS Upload ID vs Attachment ID

The current TUS API stores files and exposes them through:

```text
/files/{uploadId}
```

But it does **not** create an `Attachment` database row.

That means:

- `uploadId` is not an `Attachment.id`
- `uploadId` cannot currently be passed as `videoAttachmentId`
- `uploadId` cannot currently be passed as lesson material `attachmentId`

For Online Course lesson videos, the backend still expects:

```json
{
  "videoAttachmentId": "attachment-uuid"
}
```

Recommended backend addition:

```http
POST /api/v1/attachments/tus/{uploadId}/complete
```

That endpoint should register the completed TUS object as an `Attachment` and return the new `Attachment.id`.

## Frontend Checklist

- Read `Tus-Max-Size` with `OPTIONS /files` before allowing very large uploads.
- Always send `Tus-Resumable: 1.0.0`.
- Store `uploadUrl`/`uploadId` locally while upload is in progress.
- Resume interrupted uploads with `HEAD /files/{uploadId}`.
- Use the returned `Upload-Offset` after every chunk.
- Do not retry a failed chunk with a guessed offset; call `HEAD` first.
- Use `GET /files/{uploadId}` for completed file preview/download.
- Do not use `uploadId` as `Attachment.id` until a TUS-to-attachment finalization endpoint exists.
