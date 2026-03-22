package uz.mirmaxsudov.lmsbackend.model.request.user;

import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.TeacherPosition;

import java.util.UUID;

@Getter
@Setter
public class TeacherProfileRequest {
    private UUID userId;
    private TeacherPosition position;
}