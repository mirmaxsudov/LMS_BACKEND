package uz.mirmaxsudov.lmsbackend.model.request.lms.online;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.online.OnlineCourseContentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class OnlineCourseLessonCreateRequest {
    @NotBlank(message = "Lesson title is required")
    @Size(max = 255, message = "Lesson title must be less than or equal to 255 characters")
    private String title;

    @Size(max = 3000, message = "Description must be less than or equal to 3000 characters")
    private String description;

    @Size(max = 20000, message = "Content must be less than or equal to 20000 characters")
    private String content;

    @NotNull(message = "Order index is required")
    @PositiveOrZero(message = "Order index must be zero or greater")
    private Integer orderIndex;

    @Positive(message = "Duration must be greater than 0")
    private Integer durationInMinutes;

    private boolean freePreview;

    private OnlineCourseContentStatus status;

    private LocalDateTime availableFrom;

    private UUID videoAttachmentId;
}
