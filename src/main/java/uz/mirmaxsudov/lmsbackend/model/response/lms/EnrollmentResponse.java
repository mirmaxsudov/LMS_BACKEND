package uz.mirmaxsudov.lmsbackend.model.response.lms;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.EnrollmentStatus;
import uz.mirmaxsudov.lmsbackend.model.response.auth.AuthMe;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class EnrollmentResponse {
    private UUID id;
    private UUID groupId;
    private String groupName;
    private UUID studentProfileId;
    private UUID studentId;
    private AuthMe student;
    private EnrollmentStatus status;
    private LocalDateTime enrolledAt;
}
