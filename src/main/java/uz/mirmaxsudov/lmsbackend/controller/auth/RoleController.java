package uz.mirmaxsudov.lmsbackend.controller.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.mirmaxsudov.lmsbackend.common.util.APIUtil;
import uz.mirmaxsudov.lmsbackend.model.request.auth.RoleCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.auth.RoleUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.auth.AuthMeRole;
import uz.mirmaxsudov.lmsbackend.service.base.RoleService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(APIUtil.API_BASE_URL + "roles")
public class RoleController {
    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('PERM_USER_VIEW')")
    public ResponseEntity<ApiPaginateResponse<List<AuthMeRole>>> getAll(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "permissionId", required = false) UUID permissionId
    ) {
        return roleService.getAll(page, size, search, permissionId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('PERM_USER_VIEW')")
    public ResponseEntity<ApiResponse<AuthMeRole>> getById(@PathVariable UUID id) {
        return roleService.getByIdResponse(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('PERM_USER_EDIT')")
    public ResponseEntity<ApiResponse<AuthMeRole>> create(@RequestBody @Valid RoleCreateRequest request) {
        return roleService.createRole(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('PERM_USER_EDIT')")
    public ResponseEntity<ApiResponse<AuthMeRole>> update(
            @PathVariable UUID id,
            @RequestBody @Valid RoleUpdateRequest request
    ) {
        return roleService.updateRole(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('PERM_USER_DELETE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        return roleService.deleteRole(id);
    }
}
