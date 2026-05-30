package uz.mirmaxsudov.lmsbackend.service.base;

import uz.mirmaxsudov.lmsbackend.model.entity.auth.Role;
import org.springframework.http.ResponseEntity;
import uz.mirmaxsudov.lmsbackend.model.request.auth.RoleCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.auth.RoleRequest;
import uz.mirmaxsudov.lmsbackend.model.request.auth.RoleUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.auth.AuthMeRole;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface RoleService {
    ResponseEntity<ApiPaginateResponse<List<AuthMeRole>>> getAll(
            int page,
            int size,
            String search,
            UUID permissionId
    );

    ResponseEntity<ApiResponse<AuthMeRole>> getByIdResponse(UUID id);

    ResponseEntity<ApiResponse<AuthMeRole>> createRole(RoleCreateRequest request);

    ResponseEntity<ApiResponse<AuthMeRole>> updateRole(UUID id, RoleUpdateRequest request);

    ResponseEntity<ApiResponse<Void>> deleteRole(UUID id);

    Set<Role> resolveRoleIds(Collection<UUID> roleIds);

    Set<Role> resolveRoles(List<RoleRequest> roleRequests);

    Set<Role> getAllByUser(UUID userId);
}
