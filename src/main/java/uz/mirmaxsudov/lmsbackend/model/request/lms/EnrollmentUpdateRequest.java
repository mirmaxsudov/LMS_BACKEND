package uz.mirmaxsudov.lmsbackend.model.request.lms;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.EnrollmentStatus;

import java.util.UUID;

@Getter
@Setter
public class EnrollmentUpdateRequest {
    private UUID groupId;
    private UUID studentProfileId;

    @NotNull(message = "Enrollment status is required")
    private EnrollmentStatus status;
}
