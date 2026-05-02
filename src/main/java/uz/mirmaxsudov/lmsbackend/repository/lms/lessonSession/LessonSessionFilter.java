package uz.mirmaxsudov.lmsbackend.repository.lms.lessonSession;

import lombok.Builder;
import lombok.Getter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.LessonSessionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class LessonSessionFilter {
    private UUID groupId;
    private UUID lessonId;
    private LessonSessionStatus status;
    private LocalDateTime from;
    private LocalDateTime to;
}
