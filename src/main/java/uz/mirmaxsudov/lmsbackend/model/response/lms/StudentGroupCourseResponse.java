package uz.mirmaxsudov.lmsbackend.model.response.lms;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.CourseLevel;

import java.util.UUID;

@Getter
@Setter
@Builder
public class StudentGroupCourseResponse {
    private UUID id;
    private String title;
    private String description;
    private CourseLevel level;
    private Integer durationInMinutes;
}
