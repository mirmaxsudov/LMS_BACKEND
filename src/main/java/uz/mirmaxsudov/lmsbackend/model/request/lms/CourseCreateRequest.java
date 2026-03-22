package uz.mirmaxsudov.lmsbackend.model.request.lms;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CourseCreateRequest {
    @NotBlank(message = "Course name is required")
    private String name;

    @NotBlank(message = "Course code is required")
    private String code;

    private String description;

    private Boolean active = true;

    @NotNull(message = "Teacher id is required")
    private UUID teacherId;
}
