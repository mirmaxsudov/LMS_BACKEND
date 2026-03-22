package uz.mirmaxsudov.lmsbackend.controller.lms;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uz.mirmaxsudov.lmsbackend.common.util.APIUtil;
import uz.mirmaxsudov.lmsbackend.model.request.lms.CourseCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.CourseUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.CourseResponse;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;
import uz.mirmaxsudov.lmsbackend.service.base.lms.CourseService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(APIUtil.API_BASE_URL + "courses")
public class CourseController {
    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<ApiResponse<CourseResponse>> create(
            @RequestBody @Valid CourseCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return courseService.create(request, details);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> getById(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return courseService.getById(id, details);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> update(
            @PathVariable UUID id,
            @RequestBody @Valid CourseUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return courseService.update(id, request, details);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return courseService.delete(id, details);
    }

    @GetMapping
    public ResponseEntity<ApiPaginateResponse<List<CourseResponse>>> getPaginate(
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "teacherId", required = false) UUID teacherId,
            @RequestParam(value = "active", required = false) Boolean active,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return courseService.getPaginate(page, size, search, teacherId, active, details);
    }
}
