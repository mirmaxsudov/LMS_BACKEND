package uz.mirmaxsudov.lmsbackend.service.impl;

import io.minio.StatObjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.mirmaxsudov.lmsbackend.common.util.APIUtil;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomBadRequestException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomNotFoundException;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.entity.content.Attachment;
import uz.mirmaxsudov.lmsbackend.model.enums.content.AttachmentType;
import uz.mirmaxsudov.lmsbackend.model.response.content.AttachmentDownload;
import uz.mirmaxsudov.lmsbackend.model.response.content.AttachmentResponse;
import uz.mirmaxsudov.lmsbackend.repository.content.AttachmentRepository;
import uz.mirmaxsudov.lmsbackend.service.base.AttachmentService;
import uz.mirmaxsudov.lmsbackend.storage.StorageService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final StorageService storageService;

    @Value("${app.public-base-url:http://localhost:8888}")
    private String publicBaseUrl;

    @Override
    @Transactional
    public Attachment upload(MultipartFile file, AttachmentType type, User uploadedBy) {
        validateMultipartFile(file);

        String originalName = normalizeFileName(file.getOriginalFilename());
        String extension = extractExtension(originalName);
        String storedName = UUID.randomUUID() + (extension == null ? "" : "." + extension);
        String objectKey = "attachments/" + storedName;
        String contentType = normalizeContentType(file.getContentType());

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
                .extension(extension == null ? "bin" : extension)
                .build();

        Attachment savedAttachment = attachmentRepository.save(attachment);
        savedAttachment.setUrl(buildAttachmentUrl(savedAttachment.getId()));

        return savedAttachment;
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
    public AttachmentDownload download(UUID id) {
        Attachment attachment = attachmentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new CustomNotFoundException("Attachment not found with id: " + id));

        StatObjectResponse stat = storageService.statObject(attachment.getPath());
        String contentType = normalizeContentType(stat.contentType());

        return new AttachmentDownload(
                storageService.openObject(attachment.getPath(), 0, null),
                stat.size(),
                contentType,
                attachment.getName()
        );
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Attachment attachment = attachmentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new CustomNotFoundException("Attachment not found with id: " + id));

        if (attachment.getPath() != null && !attachment.getPath().isBlank() && storageService.objectExists(attachment.getPath()))
            storageService.removeObject(attachment.getPath());

        attachmentRepository.delete(attachment);
    }

    @Override
    public Optional<Attachment> getOptionalById(UUID id) {
        return attachmentRepository.findByIdAndDeletedFalse(id);
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

    private String buildAttachmentUrl(UUID id) {
        String baseUrl = publicBaseUrl;
        if (baseUrl.endsWith("/"))
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        return baseUrl + APIUtil.API_BASE_URL + "attachments/" + id;
    }
}
