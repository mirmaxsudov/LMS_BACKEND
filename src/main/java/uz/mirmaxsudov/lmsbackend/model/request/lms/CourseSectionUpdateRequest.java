package uz.mirmaxsudov.lmsbackend.model.request.lms;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseSectionUpdateRequest {
    @NotBlank
    @Size(max = 255)
    private String title;

    @NotNull
    @PositiveOrZero
    private Integer orderIndex;
}
