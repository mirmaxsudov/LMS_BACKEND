package uz.mirmaxsudov.lmsbackend.model.response.user.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.StudentStatus;
import uz.mirmaxsudov.lmsbackend.model.response.auth.AuthMe;

import java.util.UUID;

@Getter
@Setter
@Builder
public class StudentProfileResponse {
    private AuthMe baseData;
    private UUID studentId;
    private StudentStatus status;
}
