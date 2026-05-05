package uz.mirmaxsudov.lmsbackend.model.response.lms;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class LessonResponse {
    private UUID id;
    private String title;
    private String content;
    private Integer durationInMinutes;
    private UUID sectionId;
    private String sectionTitle;
}
