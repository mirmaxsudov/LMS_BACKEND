package uz.mirmaxsudov.lmsbackend.model.request.lms.online;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class OnlineCourseLessonMaterialRequest {
    @NotNull(message = "Attachment id is required")
    private UUID attachmentId;

    @Size(max = 255, message = "Material title must be less than or equal to 255 characters")
    private String title;

    @NotNull(message = "Order index is required")
    @PositiveOrZero(message = "Order index must be zero or greater")
    private Integer orderIndex;
}
