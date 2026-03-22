package uz.mirmaxsudov.lmsbackend.repository.lms.specification.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.EnrollmentStatus;

import java.util.UUID;

@Getter
@Setter
@Builder
public class EnrollmentFilter {
    private UUID groupId;
    private UUID studentProfileId;
    private EnrollmentStatus status;
}
