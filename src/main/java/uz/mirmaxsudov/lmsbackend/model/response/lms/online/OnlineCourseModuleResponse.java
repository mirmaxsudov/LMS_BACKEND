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
public class OnlineCourseModuleResponse {
    private UUID id;
    private UUID courseId;
    private String title;
    private String description;
    private Integer orderIndex;
    private OnlineCourseContentStatus status;
    private LocalDateTime availableFrom;
    private List<OnlineCourseLessonResponse> lessons;
}
