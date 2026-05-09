package uz.mirmaxsudov.lmsbackend.model.response.lms.online;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.CourseLevel;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.online.OnlineCourseStatus;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.online.OnlineCourseUnlockStrategy;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class OnlineCourseResponse {
    private UUID id;
    private String title;
    private String slug;
    private String shortDescription;
    private String description;
    private CourseLevel level;
    private OnlineCourseStatus status;
    private OnlineCourseUnlockStrategy unlockStrategy;
    private Integer estimatedDurationInMinutes;
    private UUID thumbnailId;
    private String thumbnailUrl;
    private UUID createdById;
    private String createdByName;
    private List<OnlineCourseModuleResponse> modules;
}
