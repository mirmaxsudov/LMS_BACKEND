package uz.mirmaxsudov.lmsbackend.model.response.lms;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CourseSectionResponse {
    private UUID id;
    private String title;
    private Integer orderIndex;
    private UUID courseId;
    private String courseTitle;
    private List<LessonResponse> lessons;
}
