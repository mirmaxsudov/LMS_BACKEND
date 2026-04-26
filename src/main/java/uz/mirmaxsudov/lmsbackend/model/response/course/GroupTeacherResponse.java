package uz.mirmaxsudov.lmsbackend.model.response.course;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.Gender;
import uz.mirmaxsudov.lmsbackend.model.enums.auth.UserStatus;

import java.util.UUID;

@Getter
@Setter
@Builder
public class GroupTeacherResponse {
    private UUID userId;
    private UUID teacherId;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String phoneNumber;
    private Gender gender;
    private UserStatus status;
    private String birthDate;
    private String profileImageUrl;
    private String profileBackgroundUrl;
}