package uz.mirmaxsudov.lmsbackend.model.response.lms.online;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.online.OnlineCourseContentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class OnlineCourseLessonResponse {
    private UUID id;
    private UUID moduleId;
    private String title;
    private String description;
    private String content;
    private Integer orderIndex;
    private Integer durationInMinutes;
    private boolean freePreview;
    private OnlineCourseContentStatus status;
    private LocalDateTime availableFrom;
    private UUID videoAttachmentId;
    private String videoUrl;
    private List<OnlineCourseLessonMaterialResponse> materials;
}
