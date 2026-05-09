package uz.mirmaxsudov.lmsbackend.model.request.lms.online;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.CourseLevel;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.online.OnlineCourseStatus;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.online.OnlineCourseUnlockStrategy;

import java.util.UUID;

@Getter
@Setter
public class OnlineCourseCreateRequest {
    @NotBlank(message = "Course title is required")
    @Size(max = 255, message = "Course title must be less than or equal to 255 characters")
    private String title;

    @Size(max = 255, message = "Slug must be less than or equal to 255 characters")
    private String slug;

    @Size(max = 500, message = "Short description must be less than or equal to 500 characters")
    private String shortDescription;

    @Size(max = 10000, message = "Description must be less than or equal to 10000 characters")
    private String description;

    @NotNull(message = "Course level is required")
    private CourseLevel level;

    private OnlineCourseStatus status;

    private OnlineCourseUnlockStrategy unlockStrategy;

    @Positive(message = "Estimated duration must be greater than 0")
    private Integer estimatedDurationInMinutes;

    private UUID thumbnailId;
}
