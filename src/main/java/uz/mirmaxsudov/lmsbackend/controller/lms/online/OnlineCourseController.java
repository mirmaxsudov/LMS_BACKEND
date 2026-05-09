package uz.mirmaxsudov.lmsbackend.controller.lms.online;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.mirmaxsudov.lmsbackend.common.util.APIUtil;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.CourseLevel;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.online.OnlineCourseEnrollmentStatus;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.online.OnlineCourseStatus;
import uz.mirmaxsudov.lmsbackend.model.request.lms.online.OnlineCourseCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.online.OnlineCourseEnrollmentCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.online.OnlineCourseLessonCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.online.OnlineCourseLessonMaterialRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.online.OnlineCourseLessonProgressRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.online.OnlineCourseLessonUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.online.OnlineCourseModuleCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.online.OnlineCourseModuleUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.online.OnlineCourseUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.online.OnlineCourseEnrollmentResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.online.OnlineCourseLessonMaterialResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.online.OnlineCourseLessonResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.online.OnlineCourseModuleResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.online.OnlineCourseResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.online.OnlineCourseSummaryResponse;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;
import uz.mirmaxsudov.lmsbackend.service.base.lms.online.OnlineCourseService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(APIUtil.API_BASE_URL + "online-courses")
public class OnlineCourseController {
    private static final String ALL_ROLES = "hasAnyRole('SUPER_ADMIN','ADMIN','TEACHER','STUDENT','PARENT','GUARDIAN','MAINTAINER','SUPPORT_TEACHER')";
    private static final String AUTHORS = "hasAnyRole('SUPER_ADMIN','ADMIN','TEACHER','MAINTAINER','SUPPORT_TEACHER')";
    private static final String MANAGERS = "hasAnyRole('SUPER_ADMIN','ADMIN','MAINTAINER')";

    private final OnlineCourseService onlineCourseService;

    @GetMapping
    @PreAuthorize(ALL_ROLES)
    public ResponseEntity<ApiPaginateResponse<List<OnlineCourseSummaryResponse>>> getAllCourses(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "level", required = false) CourseLevel level,
            @RequestParam(value = "status", required = false) OnlineCourseStatus status,
            @RequestParam(value = "createdById", required = false) UUID createdById,
            @RequestParam(value = "minDuration", required = false) Integer minDurationInMinutes,
            @RequestParam(value = "maxDuration", required = false) Integer maxDurationInMinutes,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return onlineCourseService.getAllCourses(
                page,
                size,
                search,
                level,
                status,
                createdById,
                minDurationInMinutes,
                maxDurationInMinutes,
                details
        );
    }

    @GetMapping("/{courseId}")
    @PreAuthorize(ALL_ROLES)
    public ResponseEntity<ApiResponse<OnlineCourseResponse>> getCourseById(
            @PathVariable UUID courseId,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return onlineCourseService.getCourseById(courseId, details);
    }

    @PostMapping
    @PreAuthorize(AUTHORS)
    public ResponseEntity<ApiResponse<OnlineCourseResponse>> createCourse(
            @RequestBody @Valid OnlineCourseCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return onlineCourseService.createCourse(request, details);
    }

    @PutMapping("/{courseId}")
    @PreAuthorize(AUTHORS)
    public ResponseEntity<ApiResponse<OnlineCourseResponse>> updateCourse(
            @PathVariable UUID courseId,
            @RequestBody @Valid OnlineCourseUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return onlineCourseService.updateCourse(courseId, request, details);
    }

    @DeleteMapping("/{courseId}")
    @PreAuthorize(AUTHORS)
    public ResponseEntity<ApiResponse<Void>> deleteCourse(
            @PathVariable UUID courseId,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return onlineCourseService.deleteCourse(courseId, details);
    }

    @PostMapping("/{courseId}/modules")
    @PreAuthorize(AUTHORS)
    public ResponseEntity<ApiResponse<OnlineCourseModuleResponse>> createModule(
            @PathVariable UUID courseId,
            @RequestBody @Valid OnlineCourseModuleCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return onlineCourseService.createModule(courseId, request, details);
    }

    @PutMapping("/modules/{moduleId}")
    @PreAuthorize(AUTHORS)
    public ResponseEntity<ApiResponse<OnlineCourseModuleResponse>> updateModule(
            @PathVariable UUID moduleId,
            @RequestBody @Valid OnlineCourseModuleUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return onlineCourseService.updateModule(moduleId, request, details);
    }

    @DeleteMapping("/modules/{moduleId}")
    @PreAuthorize(AUTHORS)
    public ResponseEntity<ApiResponse<Void>> deleteModule(
            @PathVariable UUID moduleId,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return onlineCourseService.deleteModule(moduleId, details);
    }

    @PostMapping("/modules/{moduleId}/lessons")
    @PreAuthorize(AUTHORS)
    public ResponseEntity<ApiResponse<OnlineCourseLessonResponse>> createLesson(
            @PathVariable UUID moduleId,
            @RequestBody @Valid OnlineCourseLessonCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return onlineCourseService.createLesson(moduleId, request, details);
    }

    @PutMapping("/lessons/{lessonId}")
    @PreAuthorize(AUTHORS)
    public ResponseEntity<ApiResponse<OnlineCourseLessonResponse>> updateLesson(
            @PathVariable UUID lessonId,
            @RequestBody @Valid OnlineCourseLessonUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return onlineCourseService.updateLesson(lessonId, request, details);
    }

    @DeleteMapping("/lessons/{lessonId}")
    @PreAuthorize(AUTHORS)
    public ResponseEntity<ApiResponse<Void>> deleteLesson(
            @PathVariable UUID lessonId,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return onlineCourseService.deleteLesson(lessonId, details);
    }

    @PostMapping("/lessons/{lessonId}/materials")
    @PreAuthorize(AUTHORS)
    public ResponseEntity<ApiResponse<OnlineCourseLessonMaterialResponse>> addLessonMaterial(
            @PathVariable UUID lessonId,
            @RequestBody @Valid OnlineCourseLessonMaterialRequest request,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return onlineCourseService.addLessonMaterial(lessonId, request, details);
    }

    @PutMapping("/materials/{materialId}")
    @PreAuthorize(AUTHORS)
    public ResponseEntity<ApiResponse<OnlineCourseLessonMaterialResponse>> updateLessonMaterial(
            @PathVariable UUID materialId,
            @RequestBody @Valid OnlineCourseLessonMaterialRequest request,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return onlineCourseService.updateLessonMaterial(materialId, request, details);
    }

    @PreAuthorize(AUTHORS)
    @DeleteMapping("/materials/{materialId}")
    public ResponseEntity<ApiResponse<Void>> deleteLessonMaterial(
            @PathVariable UUID materialId,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return onlineCourseService.deleteLessonMaterial(materialId, details);
    }

    @PostMapping("/{courseId}/enrollments/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<OnlineCourseEnrollmentResponse>> enrollMe(
            @PathVariable UUID courseId,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return onlineCourseService.enrollMe(courseId, details);
    }

    @PostMapping("/{courseId}/enrollments")
    @PreAuthorize(MANAGERS)
    public ResponseEntity<ApiResponse<OnlineCourseEnrollmentResponse>> createEnrollment(
            @PathVariable UUID courseId,
            @RequestBody @Valid OnlineCourseEnrollmentCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return onlineCourseService.createEnrollment(courseId, request, details);
    }

    @GetMapping("/enrollments/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiPaginateResponse<List<OnlineCourseEnrollmentResponse>>> getMyEnrollments(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "status", required = false) OnlineCourseEnrollmentStatus status,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return onlineCourseService.getMyEnrollments(page, size, status, details);
    }

    @GetMapping("/enrollments")
    @PreAuthorize(MANAGERS)
    public ResponseEntity<ApiPaginateResponse<List<OnlineCourseEnrollmentResponse>>> getEnrollments(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "courseId", required = false) UUID courseId,
            @RequestParam(value = "studentProfileId", required = false) UUID studentProfileId,
            @RequestParam(value = "status", required = false) OnlineCourseEnrollmentStatus status,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return onlineCourseService.getEnrollments(page, size, courseId, studentProfileId, status, details);
    }

    @GetMapping("/students/{studentProfileId}/enrollments")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','MAINTAINER','STUDENT','PARENT','GUARDIAN')")
    public ResponseEntity<ApiPaginateResponse<List<OnlineCourseEnrollmentResponse>>> getStudentEnrollments(
            @PathVariable UUID studentProfileId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "status", required = false) OnlineCourseEnrollmentStatus status,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return onlineCourseService.getStudentEnrollments(studentProfileId, page, size, status, details);
    }

    @PatchMapping("/lessons/{lessonId}/progress")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<OnlineCourseEnrollmentResponse>> updateLessonProgress(
            @PathVariable UUID lessonId,
            @RequestBody @Valid OnlineCourseLessonProgressRequest request,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return onlineCourseService.updateLessonProgress(lessonId, request, details);
    }
}
