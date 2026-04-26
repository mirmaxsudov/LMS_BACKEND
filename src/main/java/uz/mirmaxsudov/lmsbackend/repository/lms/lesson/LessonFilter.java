package uz.mirmaxsudov.lmsbackend.repository.lms.lesson;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class LessonFilter {
    private String search;
    private UUID sectionId;
    private Integer minDuration;
    private Integer maxDuration;
}
