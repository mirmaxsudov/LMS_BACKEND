package uz.mirmaxsudov.lmsbackend.model.response.content;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.content.AttachmentType;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class AttachmentResponse {
    private UUID id;
    private String originalName;
    private String storedName;
    private String path;
    private String url;
    private Long size;
    private String contentType;
    private String extension;
    private String checksum;
    private AttachmentType type;
    private UUID uploadedById;
    private LocalDateTime createdAt;
}
