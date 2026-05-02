package uz.mirmaxsudov.lmsbackend.model.request.lms;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.LessonSessionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class LessonSessionCreateRequest {
    @NotNull(message = "Group id is required")
    private UUID groupId;

    @NotNull(message = "Lesson id is required")
    private UUID lessonId;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    private LessonSessionStatus status = LessonSessionStatus.PLANNED;
}
