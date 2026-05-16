package uz.mirmaxsudov.lmsbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uz.mirmaxsudov.lmsbackend.common.util.APIUtil;
import uz.mirmaxsudov.lmsbackend.model.enums.content.AttachmentType;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.content.AttachmentDownload;
import uz.mirmaxsudov.lmsbackend.model.response.content.AttachmentResponse;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;
import uz.mirmaxsudov.lmsbackend.service.base.AttachmentService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(APIUtil.API_BASE_URL + "attachments")
public class AttachmentController {
    private final AttachmentService attachmentService;

    @PostMapping(consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<ApiResponse<AttachmentResponse>> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam(required = false) AttachmentType type,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        AttachmentResponse response = attachmentService.toResponse(
                attachmentService.upload(file, type, details.user())
        );

        return ResponseEntity.ok(ApiResponse.<AttachmentResponse>builder()
                .success(true)
                .message("Attachment uploaded successfully")
                .data(response)
                .build());
    }

    @PostMapping(value = "/bulk", consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<ApiResponse<List<AttachmentResponse>>> uploadMany(
            @RequestPart("files") List<MultipartFile> files,
            @RequestParam(required = false) AttachmentType type,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        List<AttachmentResponse> responses = attachmentService.uploadMany(files, type, details.user());

        return ResponseEntity.ok(ApiResponse.<List<AttachmentResponse>>builder()
                .success(true)
                .message("Attachments uploaded successfully")
                .data(responses)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InputStreamResource> download(
            @PathVariable UUID id
    ) {
        AttachmentDownload attachment = attachmentService.download(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.inline()
                .filename(attachment.fileName(), StandardCharsets.UTF_8)
                .build());

        MediaType mediaType;
        try {
            mediaType = MediaType.parseMediaType(attachment.contentType());
        } catch (Exception ex) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(attachment.size())
                .contentType(mediaType)
                .body(new InputStreamResource(attachment.stream()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID id
    ) {
        attachmentService.delete(id);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Attachment deleted successfully")
                .build());
    }
}
