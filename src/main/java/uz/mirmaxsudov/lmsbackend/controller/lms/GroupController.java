package uz.mirmaxsudov.lmsbackend.controller.lms;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uz.mirmaxsudov.lmsbackend.common.util.APIUtil;
import uz.mirmaxsudov.lmsbackend.model.request.lms.GroupCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.GroupUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.GroupResponse;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;
import uz.mirmaxsudov.lmsbackend.service.base.lms.GroupService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(APIUtil.API_BASE_URL + "groups")
public class GroupController {
    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<ApiResponse<GroupResponse>> create(
            @RequestBody @Valid GroupCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return groupService.create(request, details);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GroupResponse>> getById(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return groupService.getById(id, details);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GroupResponse>> update(
            @PathVariable UUID id,
            @RequestBody @Valid GroupUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return groupService.update(id, request, details);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return groupService.delete(id, details);
    }

    @GetMapping
    public ResponseEntity<ApiPaginateResponse<List<GroupResponse>>> getPaginate(
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "courseId", required = false) UUID courseId,
            @RequestParam(value = "teacherId", required = false) UUID teacherId,
            @RequestParam(value = "active", required = false) Boolean active,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return groupService.getPaginate(page, size, search, courseId, teacherId, active, details);
    }
}
