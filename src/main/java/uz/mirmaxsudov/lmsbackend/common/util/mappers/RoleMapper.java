package uz.mirmaxsudov.lmsbackend.common.util.mappers;

import uz.mirmaxsudov.lmsbackend.model.entity.auth.Role;
import uz.mirmaxsudov.lmsbackend.model.response.auth.AuthMeRole;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public final class RoleMapper {
    public static Set<AuthMeRole> toResponses(Set<Role> roles) {
        if (roles == null || roles.isEmpty())
            return Collections.emptySet();

        return roles.stream()
                .filter(role -> role != null && !role.isDeleted())
                .map(RoleMapper::toResponse)
                .collect(Collectors.toSet());
    }

    public static AuthMeRole toResponse(Role role) {
        return AuthMeRole.builder()
                .id(role.getId())
                .updatedAt(role.getUpdatedAt())
                .createdAt(role.getCreatedAt())
                .name(role.getName())
                .description(role.getDescription())
                .permissions(PermissionMapper.toResponses(role.getPermissions()))
                .build();
    }
}
