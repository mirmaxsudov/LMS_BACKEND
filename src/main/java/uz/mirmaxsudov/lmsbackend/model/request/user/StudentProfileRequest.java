package uz.mirmaxsudov.lmsbackend.model.request.user;

import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.StudentStatus;

import java.util.UUID;

@Getter
@Setter
public class StudentProfileRequest extends UserCreateRequest {
    private StudentStatus studentStatus;
}
