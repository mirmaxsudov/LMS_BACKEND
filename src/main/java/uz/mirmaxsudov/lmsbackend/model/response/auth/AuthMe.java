package uz.mirmaxsudov.lmsbackend.model.response.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.Role;
import uz.mirmaxsudov.lmsbackend.model.enums.Gender;
import uz.mirmaxsudov.lmsbackend.model.enums.auth.UserStatus;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
public class AuthMe {
    private UUID id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String phoneNumber;
    private Gender gender;
    private UserStatus status;
    private LocalDateTime birthDate;
    private UUID profileImageAttachmentId;
    private String profileImageUrl;
    private UUID profileBackgroundAttachmentId;
    private String profileBackgroundUrl;
    private Set<AuthMeRole> roles;
}
