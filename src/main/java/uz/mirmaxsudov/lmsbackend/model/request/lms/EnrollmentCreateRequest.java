package uz.mirmaxsudov.lmsbackend.model.request.lms;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class EnrollmentCreateRequest {
    private UUID groupId;
    private UUID studentProfileId;
}
