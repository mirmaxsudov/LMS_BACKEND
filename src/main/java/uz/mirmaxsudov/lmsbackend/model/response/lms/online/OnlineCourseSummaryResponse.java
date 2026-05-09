package uz.mirmaxsudov.lmsbackend.model.response.lms.online;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.CourseLevel;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.online.OnlineCourseStatus;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.online.OnlineCourseUnlockStrategy;

import java.util.UUID;

@Getter
@Setter
@Builder
public class OnlineCourseSummaryResponse {
    private UUID id;
    private String title;
    private String slug;
    private String shortDescription;
    private CourseLevel level;
    private OnlineCourseStatus status;
    private OnlineCourseUnlockStrategy unlockStrategy;
    private Integer estimatedDurationInMinutes;
    private UUID thumbnailId;
    private String thumbnailUrl;
}
