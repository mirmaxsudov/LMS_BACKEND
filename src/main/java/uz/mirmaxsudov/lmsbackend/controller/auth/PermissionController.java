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
import uz.mirmaxsudov.lmsbackend.model.enums.auth.PermissionCategory;
import uz.mirmaxsudov.lmsbackend.model.request.auth.PermissionCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.auth.PermissionUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.auth.AuthMePermission;
import uz.mirmaxsudov.lmsbackend.service.base.PermissionService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(APIUtil.API_BASE_URL + "permissions")
public class PermissionController {
    private final PermissionService permissionService;

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('PERM_USER_VIEW')")
    public ResponseEntity<ApiPaginateResponse<List<AuthMePermission>>> getAll(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "module", required = false) String module,
            @RequestParam(value = "action", required = false) String action,
            @RequestParam(value = "category", required = false) PermissionCategory category,
            @RequestParam(value = "isSystem", required = false) Boolean isSystem
    ) {
        return permissionService.getAll(page, size, search, module, action, category, isSystem);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('PERM_USER_VIEW')")
    public ResponseEntity<ApiResponse<AuthMePermission>> getById(@PathVariable UUID id) {
        return permissionService.getByIdResponse(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('PERM_USER_EDIT')")
    public ResponseEntity<ApiResponse<AuthMePermission>> create(@RequestBody @Valid PermissionCreateRequest request) {
        return permissionService.createPermission(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('PERM_USER_EDIT')")
    public ResponseEntity<ApiResponse<AuthMePermission>> update(
            @PathVariable UUID id,
            @RequestBody @Valid PermissionUpdateRequest request
    ) {
        return permissionService.updatePermission(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('PERM_USER_DELETE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        return permissionService.deletePermission(id);
    }
}