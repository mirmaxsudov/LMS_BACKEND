package uz.mirmaxsudov.lmsbackend.model.request.auth;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class UserRolesUpdateRequest {
    private Set<UUID> roleIds = new LinkedHashSet<>();
}
