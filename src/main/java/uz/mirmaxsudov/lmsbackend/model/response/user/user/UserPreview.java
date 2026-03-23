package uz.mirmaxsudov.lmsbackend.model.response.user.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.auth.UserStatus;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
public class UserPreview {
    private UUID id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String phoneNumber;
    private UserStatus status;
    private String profileImageUrl;
    private String profileBackgroundUrl;
    private Set<String> roles;
}
