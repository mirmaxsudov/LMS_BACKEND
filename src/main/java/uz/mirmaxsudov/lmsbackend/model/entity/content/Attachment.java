package uz.mirmaxsudov.lmsbackend.model.entity.content;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.entity.base.BaseEntity;
import uz.mirmaxsudov.lmsbackend.model.enums.content.AttachmentType;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "attachments", indexes = {
        @Index(columnList = "id"),
        @Index(columnList = "type"),
        @Index(columnList = "uploaded_by_id")
})
public class Attachment extends BaseEntity {
    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false)
    private String storedName;

    @Column(nullable = false)
    private String path;

    private String url;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private String contentType;

    private String extension;

    private String checksum;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttachmentType type = AttachmentType.OTHER;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_id")
    private User uploadedBy;
}
