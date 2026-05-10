package uz.mirmaxsudov.lmsbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.mirmaxsudov.lmsbackend.config.minio.MinioProperties;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomBadRequestException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomNotFoundException;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.entity.content.Attachment;
import uz.mirmaxsudov.lmsbackend.model.enums.content.AttachmentType;
import uz.mirmaxsudov.lmsbackend.model.response.content.AttachmentResponse;
import uz.mirmaxsudov.lmsbackend.repository.content.AttachmentRepository;
import uz.mirmaxsudov.lmsbackend.service.base.AttachmentService;
import uz.mirmaxsudov.lmsbackend.storage.StorageService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final StorageService storageService;
    private final MinioProperties minioProperties;

    @Override
    @Transactional
    public Attachment upload(MultipartFile file, AttachmentType type, User uploadedBy) {
        validateMultipartFile(file);

        String originalName = normalizeFileName(file.getOriginalFilename());
        String extension = extractExtension(originalName);
        String storedName = UUID.randomUUID() + (extension == null ? "" : "." + extension);
        String objectKey = "attachments/" + storedName;
        String contentType = normalizeContentType(file.getContentType());
        AttachmentType resolvedType = type == null ? resolveType(contentType) : type;

        try (InputStream inputStream = file.getInputStream()) {
            storageService.uploadObject(objectKey, inputStream, file.getSize(), contentType, Map.of(
                    "originalName", originalName
            ));
        } catch (IOException exception) {
            throw new CustomBadRequestException("Failed to read uploaded file");
        }

        Attachment attachment = Attachment.builder()
                .name(originalName)
                .storedName(storedName)
                .path(objectKey)
                .url(buildObjectUrl(objectKey))
                .extension(extension == null ? "bin" : extension)
                .build();

        return attachmentRepository.save(attachment);
    }

    @Override
    @Transactional
    public List<AttachmentResponse> uploadMany(List<MultipartFile> files, AttachmentType type, User uploadedBy) {
        if (files == null || files.isEmpty())
            throw new CustomBadRequestException("Files must not be empty");

        return files.stream()
                .map(file -> toResponse(upload(file, type, uploadedBy)))
                .toList();
    }

    @Override
    public AttachmentResponse toResponse(Attachment attachment) {
        return AttachmentResponse.builder()
                .id(attachment.getId())
                .originalName(attachment.getName())
                .storedName(attachment.getStoredName())
                .path(attachment.getPath())
                .url(attachment.getUrl())
                .extension(attachment.getExtension())
                .createdAt(attachment.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new CustomNotFoundException("Attachment not found with id: " + id));

        if (attachment.getPath() != null && !attachment.getPath().isBlank() && storageService.objectExists(attachment.getPath()))
            storageService.removeObject(attachment.getPath());

        attachmentRepository.delete(attachment);
    }

    private void validateMultipartFile(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new CustomBadRequestException("File must not be empty");

        if (file.getSize() <= 0)
            throw new CustomBadRequestException("File size must be greater than 0");
    }

    private String normalizeFileName(String originalFileName) {
        if (originalFileName == null || originalFileName.isBlank())
            return "file";
        return originalFileName.trim();
    }

    private String extractExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == fileName.length() - 1)
            return null;
        return fileName.substring(dotIndex + 1).toLowerCase();
    }

    private String normalizeContentType(String contentType) {
        if (contentType == null || contentType.isBlank())
            return "application/octet-stream";
        return contentType;
    }

    private AttachmentType resolveType(String contentType) {
        if (contentType.startsWith("image/"))
            return AttachmentType.IMAGE;
        if (contentType.startsWith("video/"))
            return AttachmentType.VIDEO;
        if (contentType.startsWith("audio/"))
            return AttachmentType.AUDIO;
        if (contentType.equals("application/pdf")
                || contentType.equals("application/msword")
                || contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
            return AttachmentType.DOCUMENT;
        return AttachmentType.FILE;
    }

    private String buildObjectUrl(String objectKey) {
        String endpoint = minioProperties.getEndpoint();
        if (endpoint.endsWith("/"))
            endpoint = endpoint.substring(0, endpoint.length() - 1);
        return endpoint + "/" + minioProperties.getBucket() + "/" + objectKey;
    }
}
