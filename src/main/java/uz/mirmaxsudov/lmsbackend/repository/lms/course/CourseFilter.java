package uz.mirmaxsudov.lmsbackend.repository.lms.course;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.CourseLevel;

@Getter
@Setter
@Builder
public class CourseFilter {
    private String search;
    private CourseLevel level;
    private Integer minDurationInMinutes;
    private Integer maxDurationInMinutes;
}
