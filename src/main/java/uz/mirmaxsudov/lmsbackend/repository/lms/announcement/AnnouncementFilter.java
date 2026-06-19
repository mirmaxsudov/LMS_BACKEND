package uz.mirmaxsudov.lmsbackend.repository.lms.announcement;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.AnnouncementAudience;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.AnnouncementPriority;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.AnnouncementStatus;

import java.util.Set;
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

    /**
     * Audiences the current viewer is allowed to see. When non-null, results are
     * restricted to announcements targeting at least one of these audiences.
     * Null means no audience-based access restriction (e.g. admins).
     */
    private Set<AnnouncementAudience> viewerAudiences;
}
