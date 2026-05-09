package uz.mirmaxsudov.lmsbackend.model.request.lms.online;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.online.OnlineCourseContentStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class OnlineCourseModuleUpdateRequest {
    @NotBlank(message = "Module title is required")
    @Size(max = 255, message = "Module title must be less than or equal to 255 characters")
    private String title;

    @Size(max = 3000, message = "Description must be less than or equal to 3000 characters")
    private String description;

    @NotNull(message = "Order index is required")
    @PositiveOrZero(message = "Order index must be zero or greater")
    private Integer orderIndex;

    @NotNull(message = "Module status is required")
    private OnlineCourseContentStatus status;

    private LocalDateTime availableFrom;
}
