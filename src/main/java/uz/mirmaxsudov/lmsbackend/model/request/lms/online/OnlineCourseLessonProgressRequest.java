package uz.mirmaxsudov.lmsbackend.model.request.lms.online;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.online.OnlineCourseProgressStatus;

@Getter
@Setter
public class OnlineCourseLessonProgressRequest {
    @NotNull(message = "Progress status is required")
    private OnlineCourseProgressStatus status;

    @PositiveOrZero(message = "Last position must be zero or greater")
    private Integer lastPositionInSeconds;
}
