package uz.mirmaxsudov.lmsbackend.controller.lms;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uz.mirmaxsudov.lmsbackend.common.util.APIUtil;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.EnrollmentStatus;
import uz.mirmaxsudov.lmsbackend.model.request.lms.EnrollmentCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.EnrollmentUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.EnrollmentResponse;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;
import uz.mirmaxsudov.lmsbackend.service.base.lms.EnrollmentService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(APIUtil.API_BASE_URL + "enrollments")
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<EnrollmentResponse>> create(
            @RequestBody @Valid EnrollmentCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return enrollmentService.create(request, details);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> getById(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return enrollmentService.getById(id, details);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> update(
            @PathVariable UUID id,
            @RequestBody @Valid EnrollmentUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return enrollmentService.update(id, request, details);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return enrollmentService.delete(id, details);
    }

    @GetMapping
    public ResponseEntity<ApiPaginateResponse<List<EnrollmentResponse>>> getPaginate(
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size,
            @RequestParam(value = "groupId", required = false) UUID groupId,
            @RequestParam(value = "studentProfileId", required = false) UUID studentProfileId,
            @RequestParam(value = "status", required = false) EnrollmentStatus status,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return enrollmentService.getPaginate(page, size, groupId, studentProfileId, status, details);
    }
}
