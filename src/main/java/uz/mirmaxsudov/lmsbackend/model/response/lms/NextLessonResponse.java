package uz.mirmaxsudov.lmsbackend.model.response.lms;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class NextLessonResponse {
    private UUID lessonSessionId;
    private UUID lessonId;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
