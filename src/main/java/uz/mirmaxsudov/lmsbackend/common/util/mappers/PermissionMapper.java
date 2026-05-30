package uz.mirmaxsudov.lmsbackend.common.util.mappers;

import uz.mirmaxsudov.lmsbackend.model.entity.auth.Permission;
import uz.mirmaxsudov.lmsbackend.model.response.auth.AuthMePermission;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public final class PermissionMapper {
    public static Set<AuthMePermission> toResponses(Set<Permission> permissions) {
        if (permissions == null || permissions.isEmpty())
            return Collections.emptySet();

        return permissions.stream()
                .filter(permission -> permission != null && !permission.isDeleted())
                .map(PermissionMapper::toResponse)
                .collect(Collectors.toSet());
    }

    public static AuthMePermission toResponse(Permission permission) {
        return AuthMePermission.builder()
                .id(permission.getId())
                .updatedAt(permission.getUpdatedAt())
                .createdAt(permission.getCreatedAt())
                .code(permission.getCode())
                .description(permission.getDescription())
                .module(permission.getModule())
                .action(permission.getAction())
                .category(permission.getCategory())
                .isSystem(permission.getIsSystem())
                .build();
    }
}
