package uz.mirmaxsudov.lmsbackend.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.mirmaxsudov.lmsbackend.common.util.APIUtil;
import uz.mirmaxsudov.lmsbackend.model.enums.auth.SystemRole;
import uz.mirmaxsudov.lmsbackend.model.enums.auth.UserStatus;
import uz.mirmaxsudov.lmsbackend.model.request.auth.UserRolesUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.user.UserCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.auth.AuthMe;
import uz.mirmaxsudov.lmsbackend.model.response.auth.AuthMeRole;
import uz.mirmaxsudov.lmsbackend.model.response.user.user.UserPreview;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;
import uz.mirmaxsudov.lmsbackend.service.base.UserService;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(APIUtil.API_BASE_URL + "user")
public class UserController {
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN') or hasAuthority('PERM_USER_VIEW')")
    public ResponseEntity<ApiPaginateResponse<List<UserPreview>>> getAll(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "role", required = false) SystemRole role,
            @RequestParam(value = "status", required = false) UserStatus status,
            @RequestParam(value = "permission", required = false) UUID permission
    ) {
        return userService.getAll(page, size, search, role, status, permission);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN') or hasAuthority('PERM_USER_CREATE')")
    public ResponseEntity<ApiResponse<AuthMe>> createUser(
            @ModelAttribute @Valid UserCreateRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart(value = "profileBackgroundAttachment", required = false) MultipartFile profileBackgroundAttachment,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return userService.createUser(request, profileImage, profileBackgroundAttachment, details);
    }

    @GetMapping("/{id}/roles")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN') or hasAuthority('PERM_USER_VIEW')")
    public ResponseEntity<ApiResponse<Set<AuthMeRole>>> getRoles(@PathVariable UUID id) {
        return userService.getUserRoles(id);
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN') or hasAuthority('PERM_USER_EDIT')")
    public ResponseEntity<ApiResponse<AuthMe>> updateRoles(
            @PathVariable UUID id,
            @RequestBody @Valid UserRolesUpdateRequest request
    ) {
        return userService.updateUserRoles(id, request);
    }

    @PostMapping("/{id}/roles/{roleId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN') or hasAuthority('PERM_USER_EDIT')")
    public ResponseEntity<ApiResponse<AuthMe>> addRole(
            @PathVariable UUID id,
            @PathVariable UUID roleId
    ) {
        return userService.addUserRole(id, roleId);
    }

    @DeleteMapping("/{id}/roles/{roleId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN') or hasAuthority('PERM_USER_EDIT')")
    public ResponseEntity<ApiResponse<AuthMe>> removeRole(
            @PathVariable UUID id,
            @PathVariable UUID roleId
    ) {
        return userService.removeUserRole(id, roleId);
    }
}
