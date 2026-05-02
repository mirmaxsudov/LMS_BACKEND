package uz.mirmaxsudov.lmsbackend.model.response.lms;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.LessonSessionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class LessonSessionResponse {
    private UUID id;
    private UUID groupId;
    private String groupName;
    private UUID lessonId;
    private String lessonTitle;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LessonSessionStatus status;
}
