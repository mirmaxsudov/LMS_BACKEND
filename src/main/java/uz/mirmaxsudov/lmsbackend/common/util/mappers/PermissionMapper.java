package uz.mirmaxsudov.lmsbackend.common.util.mappers;

import uz.mirmaxsudov.lmsbackend.model.entity.auth.Permission;
import uz.mirmaxsudov.lmsbackend.model.response.auth.AuthMePermission;

import java.util.Set;
import java.util.stream.Collectors;

public final class PermissionMapper {
    public static Set<AuthMePermission> toResponses(Set<Permission> permissions) {
        return permissions.stream().map(PermissionMapper::toResponse).collect(Collectors.toSet());
    }

    public static AuthMePermission toResponse(Permission permission) {
        return AuthMePermission.builder()
                .id(permission.getId())
                .updatedAt(permission.getUpdatedAt())
                .createdAt(permission.getCreatedAt())
                .code(permission.getCode())
                .description(permission.getDescription())
                .module(permission.getModule())
                .category(permission.getCategory())
                .isSystem(permission.getIsSystem())
                .build();
    }
}
