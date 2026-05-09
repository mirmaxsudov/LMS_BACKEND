package uz.mirmaxsudov.lmsbackend.service.base.lms.online;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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

import java.util.List;
import java.util.UUID;

public interface OnlineCourseService {
    ResponseEntity<ApiPaginateResponse<List<OnlineCourseSummaryResponse>>> getAllCourses(
            int page,
            int size,
            String search,
            CourseLevel level,
            OnlineCourseStatus status,
            UUID createdById,
            Integer minDurationInMinutes,
            Integer maxDurationInMinutes,
            CustomUserDetails details
    );

    ResponseEntity<ApiResponse<OnlineCourseResponse>> getCourseById(UUID courseId, CustomUserDetails details);

    ResponseEntity<ApiResponse<OnlineCourseResponse>> createCourse(@Valid OnlineCourseCreateRequest request, CustomUserDetails details);

    ResponseEntity<ApiResponse<OnlineCourseResponse>> updateCourse(UUID courseId, @Valid OnlineCourseUpdateRequest request, CustomUserDetails details);

    ResponseEntity<ApiResponse<Void>> deleteCourse(UUID courseId, CustomUserDetails details);

    ResponseEntity<ApiResponse<OnlineCourseModuleResponse>> createModule(UUID courseId, @Valid OnlineCourseModuleCreateRequest request, CustomUserDetails details);

    ResponseEntity<ApiResponse<OnlineCourseModuleResponse>> updateModule(UUID moduleId, @Valid OnlineCourseModuleUpdateRequest request, CustomUserDetails details);

    ResponseEntity<ApiResponse<Void>> deleteModule(UUID moduleId, CustomUserDetails details);

    ResponseEntity<ApiResponse<OnlineCourseLessonResponse>> createLesson(UUID moduleId, @Valid OnlineCourseLessonCreateRequest request, CustomUserDetails details);

    ResponseEntity<ApiResponse<OnlineCourseLessonResponse>> updateLesson(UUID lessonId, @Valid OnlineCourseLessonUpdateRequest request, CustomUserDetails details);

    ResponseEntity<ApiResponse<Void>> deleteLesson(UUID lessonId, CustomUserDetails details);

    ResponseEntity<ApiResponse<OnlineCourseLessonMaterialResponse>> addLessonMaterial(UUID lessonId, @Valid OnlineCourseLessonMaterialRequest request, CustomUserDetails details);

    ResponseEntity<ApiResponse<OnlineCourseLessonMaterialResponse>> updateLessonMaterial(UUID materialId, @Valid OnlineCourseLessonMaterialRequest request, CustomUserDetails details);

    ResponseEntity<ApiResponse<Void>> deleteLessonMaterial(UUID materialId, CustomUserDetails details);

    ResponseEntity<ApiResponse<OnlineCourseEnrollmentResponse>> enrollMe(UUID courseId, CustomUserDetails details);

    ResponseEntity<ApiResponse<OnlineCourseEnrollmentResponse>> createEnrollment(UUID courseId, @Valid OnlineCourseEnrollmentCreateRequest request, CustomUserDetails details);

    ResponseEntity<ApiPaginateResponse<List<OnlineCourseEnrollmentResponse>>> getMyEnrollments(
            int page,
            int size,
            OnlineCourseEnrollmentStatus status,
            CustomUserDetails details
    );

    ResponseEntity<ApiPaginateResponse<List<OnlineCourseEnrollmentResponse>>> getEnrollments(
            int page,
            int size,
            UUID courseId,
            UUID studentProfileId,
            OnlineCourseEnrollmentStatus status,
            CustomUserDetails details
    );

    ResponseEntity<ApiPaginateResponse<List<OnlineCourseEnrollmentResponse>>> getStudentEnrollments(
            UUID studentProfileId,
            int page,
            int size,
            OnlineCourseEnrollmentStatus status,
            CustomUserDetails details
    );

    ResponseEntity<ApiResponse<OnlineCourseEnrollmentResponse>> updateLessonProgress(UUID lessonId, @Valid OnlineCourseLessonProgressRequest request, CustomUserDetails details);
}
