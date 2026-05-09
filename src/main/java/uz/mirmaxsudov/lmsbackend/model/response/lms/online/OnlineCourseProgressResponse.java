package uz.mirmaxsudov.lmsbackend.model.response.lms.online;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.online.OnlineCourseProgressStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class OnlineCourseProgressResponse {
    private UUID id;
    private UUID enrollmentId;
    private UUID contentId;
    private String title;
    private OnlineCourseProgressStatus status;
    private Integer lastPositionInSeconds;
    private LocalDateTime openedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}
