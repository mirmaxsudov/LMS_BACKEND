package uz.mirmaxsudov.lmsbackend.model.response.lms;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.AnnouncementAudience;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.AnnouncementPriority;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.AnnouncementStatus;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
public class AnnouncementResponse {
    private UUID id;
    private String title;
    private String content;
    private AnnouncementStatus status;
    private AnnouncementPriority priority;
    private Set<AnnouncementAudience> audiences;
    private boolean pinned;
    private long viewCount;
    private LocalDateTime publishedAt;

    private UUID authorId;
    private String authorName;
    private String authorRole;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
