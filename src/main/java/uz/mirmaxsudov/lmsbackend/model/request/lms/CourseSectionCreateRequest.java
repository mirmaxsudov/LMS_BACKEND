package uz.mirmaxsudov.lmsbackend.model.request.lms;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CourseSectionCreateRequest {
    @NotBlank
    @Size(max = 255)
    private String title;

    @NotNull
    private UUID courseId;

    @NotNull
    @PositiveOrZero
    private Integer orderIndex;
}
