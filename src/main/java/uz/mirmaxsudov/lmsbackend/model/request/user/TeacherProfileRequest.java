package uz.mirmaxsudov.lmsbackend.model.request.user;

import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.TeacherPosition;

@Getter
@Setter
public class TeacherProfileRequest extends UserCreateRequest {
    private TeacherPosition position;
}
