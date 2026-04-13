package uz.mirmaxsudov.lmsbackend.model.request.lms;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.CourseLevel;

@Getter
@Setter
public class CourseUpdateRequest {
    @NotBlank(message = "Course title is required")
    @Size(max = 255, message = "Course title must be less than or equal to 255 characters")
    private String title;

    @Size(max = 3000, message = "Description must be less than or equal to 3000 characters")
    private String description;

    @NotNull(message = "Course level is required")
    private CourseLevel level;

    @NotNull(message = "Course duration is required")
    @Positive(message = "Course duration must be greater than 0")
    private Integer durationInMinutes;
}
