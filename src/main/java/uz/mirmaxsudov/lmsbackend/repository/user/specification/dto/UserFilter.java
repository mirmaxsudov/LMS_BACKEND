package uz.mirmaxsudov.lmsbackend.repository.user.specification.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.auth.SystemRole;
import uz.mirmaxsudov.lmsbackend.model.enums.auth.UserStatus;

import java.util.UUID;

@Getter
@Setter
@Builder
public class UserFilter {
    private String search;
    private SystemRole role;
    private UserStatus status;
    private UUID permissionId;
}
