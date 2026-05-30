package uz.mirmaxsudov.lmsbackend.service.base;

import uz.mirmaxsudov.lmsbackend.model.entity.auth.Permission;
import org.springframework.http.ResponseEntity;
import uz.mirmaxsudov.lmsbackend.model.enums.auth.PermissionCategory;
import uz.mirmaxsudov.lmsbackend.model.request.auth.PermissionCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.auth.PermissionUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.auth.AuthMePermission;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PermissionService {
    ResponseEntity<ApiPaginateResponse<List<AuthMePermission>>> getAll(
            int page,
            int size,
            String search,
            String module,
            String action,
            PermissionCategory category,
            Boolean isSystem
    );

    ResponseEntity<ApiResponse<AuthMePermission>> getByIdResponse(UUID id);

    ResponseEntity<ApiResponse<AuthMePermission>> createPermission(PermissionCreateRequest request);

    ResponseEntity<ApiResponse<AuthMePermission>> updatePermission(UUID id, PermissionUpdateRequest request);

    ResponseEntity<ApiResponse<Void>> deletePermission(UUID id);

    Set<Permission> resolvePermissionIds(Collection<UUID> permissionIds);

    Set<Permission> getAllByUser(UUID userId);
}
