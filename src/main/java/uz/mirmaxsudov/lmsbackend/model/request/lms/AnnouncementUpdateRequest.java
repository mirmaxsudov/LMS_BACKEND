package uz.mirmaxsudov.lmsbackend.model.request.lms;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.AnnouncementAudience;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.AnnouncementPriority;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class AnnouncementUpdateRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(max = 5000, message = "Content must not exceed 5000 characters")
    private String content;

    @NotNull(message = "Priority is required")
    private AnnouncementPriority priority;

    @NotEmpty(message = "At least one audience is required")
    private Set<AnnouncementAudience> audiences;

    private boolean pinned = false;

    private LocalDateTime publishedAt;
}
