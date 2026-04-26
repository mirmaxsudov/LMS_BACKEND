package uz.mirmaxsudov.lmsbackend.model.request.lms;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class LessonCreateRequest {
    @NotBlank
    @Size(max = 255)
    private String title;

    @Size(max = 20000)
    private String content;

    @NotNull
    @Positive
    private Integer durationInMinutes;

    @NotNull
    private UUID sectionId;
}
