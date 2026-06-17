package uz.mirmaxsudov.lmsbackend.repository.lms.announcement;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.AnnouncementAudience;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.AnnouncementPriority;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.AnnouncementStatus;

import java.util.UUID;

@Getter
@Setter
@Builder
public class AnnouncementFilter {
    private String search;
    private AnnouncementStatus status;
    private AnnouncementPriority priority;
    private AnnouncementAudience audience;
    private Boolean pinned;
    private UUID authorId;
}
