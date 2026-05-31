package uz.mirmaxsudov.lmsbackend.storage;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.mirmaxsudov.lmsbackend.config.minio.MinioProperties;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    @PostConstruct
    public void initBucket() {
        ensureBucketExists();
    }

    public void ensureBucketExists() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(minioProperties.getBucket()).build()
            );

            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioProperties.getBucket()).build());
                log.info("Created MinIO bucket '{}'", minioProperties.getBucket());
            }
        } catch (Exception e) {
            throw new StorageException("Failed to initialize MinIO bucket", e);
        }
    }

    public void uploadObject(String objectKey, InputStream inputStream, long size, String contentType, Map<String, String> metadata) {
        try {
            PutObjectArgs.Builder builder = PutObjectArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .object(objectKey)
                    .stream(inputStream, size, -1)
                    .contentType(contentType == null || contentType.isBlank() ? "application/octet-stream" : contentType);

            if (metadata != null && !metadata.isEmpty())
                builder.userMetadata(metadata);

            minioClient.putObject(builder.build());
            statObject(objectKey);
        } catch (Exception e) {
            throw new StorageException("Failed to upload object: " + objectKey, e);
        }
    }

    public void composeObject(String targetObjectKey, List<String> sourceObjectKeys) {
        try {
            List<ComposeSource> sources = sourceObjectKeys.stream()
                    .map(objectKey -> ComposeSource.builder()
                            .bucket(minioProperties.getBucket())
                            .object(objectKey)
                            .build())
                    .toList();

            minioClient.composeObject(
                    ComposeObjectArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .object(targetObjectKey)
                            .sources(sources)
                            .build()
            );
        } catch (Exception e) {
            throw new StorageException("Failed to compose object: " + targetObjectKey, e);
        }
    }

    public InputStream openObject(String objectKey, long offset, Long length) {
        try {
            GetObjectArgs.Builder builder = GetObjectArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .object(objectKey);

            if (offset > 0) {
                builder.offset(offset);
            }
            if (length != null && length > 0) {
                builder.length(length);
            }

            return minioClient.getObject(builder.build());
        } catch (Exception e) {
            if (isObjectNotFound(e))
                throw new StorageObjectNotFoundException("Storage object not found: " + objectKey, e);
            throw new StorageException("Failed to read object: " + objectKey, e);
        }
    }

    public StatObjectResponse statObject(String objectKey) {
        try {
            return minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .object(objectKey)
                            .build()
            );
        } catch (Exception e) {
            if (isObjectNotFound(e))
                throw new StorageObjectNotFoundException("Storage object not found: " + objectKey, e);
            throw new StorageException("Failed to stat object: " + objectKey, e);
        }
    }

    public void removeObject(String objectKey) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .object(objectKey)
                            .build()
            );
        } catch (Exception e) {
            if (isObjectNotFound(e)) {
                log.info("Storage object '{}' was already absent during delete", objectKey);
                return;
            }
            throw new StorageException("Failed to delete object: " + objectKey, e);
        }
    }

    public void removeObjects(List<String> objectKeys) {
        for (String objectKey : objectKeys) {
            try {
                removeObject(objectKey);
            } catch (StorageException ex) {
                log.warn("Failed to remove object '{}' during cleanup: {}", objectKey, ex.getMessage());
            }
        }
    }

    public boolean objectExists(String objectKey) {
        try {
            statObject(objectKey);
            return true;
        } catch (StorageObjectNotFoundException ex) {
            return false;
        } catch (StorageException ex) {
            throw ex;
        }
    }

    private boolean isObjectNotFound(Exception exception) {
        if (exception instanceof ErrorResponseException errorResponseException) {
            String code = errorResponseException.errorResponse().code();
            return "NoSuchKey".equals(code) || "NoSuchObject".equals(code);
        }
        return false;
    }
}
