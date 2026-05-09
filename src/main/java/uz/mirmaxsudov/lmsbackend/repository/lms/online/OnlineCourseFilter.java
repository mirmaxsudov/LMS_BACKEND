package uz.mirmaxsudov.lmsbackend.repository.lms.online;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.CourseLevel;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.online.OnlineCourseStatus;

import java.util.UUID;

@Getter
@Setter
@Builder
public class OnlineCourseFilter {
    private String search;
    private CourseLevel level;
    private OnlineCourseStatus status;
    private UUID createdById;
    private Integer minDurationInMinutes;
    private Integer maxDurationInMinutes;
}
