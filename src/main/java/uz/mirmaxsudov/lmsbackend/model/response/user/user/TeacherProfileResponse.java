package uz.mirmaxsudov.lmsbackend.model.response.user.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.TeacherPosition;
import uz.mirmaxsudov.lmsbackend.model.response.auth.AuthMe;

@Getter
@Setter
@Builder
public class TeacherProfileResponse {
    private AuthMe baseData;
    private TeacherPosition position;
}