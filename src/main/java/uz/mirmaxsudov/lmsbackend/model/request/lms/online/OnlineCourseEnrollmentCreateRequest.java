package uz.mirmaxsudov.lmsbackend.model.request.lms.online;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.online.OnlineCourseEnrollmentStatus;

import java.util.UUID;

@Getter
@Setter
public class OnlineCourseEnrollmentCreateRequest {
    @NotNull(message = "Student profile id is required")
    private UUID studentProfileId;

    private OnlineCourseEnrollmentStatus status;
}
