package uz.mirmaxsudov.lmsbackend.model.request.user;

import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.Gender;
import uz.mirmaxsudov.lmsbackend.model.enums.auth.UserStatus;
import uz.mirmaxsudov.lmsbackend.model.request.auth.RoleRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserRequest {
    private String firstName;
    private String lastName;
    private String middleName;
    private Gender gender;
    private LocalDateTime birthDate;
    private String phoneNumber;
    private String email;
    private String password;
    private UserStatus status = UserStatus.ACTIVE;
    private List<RoleRequest> roles = new ArrayList<>();
}