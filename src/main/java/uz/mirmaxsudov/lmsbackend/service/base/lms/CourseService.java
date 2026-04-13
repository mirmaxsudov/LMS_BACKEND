package uz.mirmaxsudov.lmsbackend.service.base.lms;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Course;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.CourseLevel;
import uz.mirmaxsudov.lmsbackend.model.request.lms.CourseCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.CourseUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.CourseResponse;
import uz.mirmaxsudov.lmsbackend.service.base.BaseCRUDService;

import java.util.List;
import java.util.UUID;

public interface CourseService extends BaseCRUDService<Course> {
    ResponseEntity<ApiPaginateResponse<List<CourseResponse>>> getAll(
            int page,
            int size,
            String search,
            CourseLevel level,
            Integer minDurationInMinutes,
            Integer maxDurationInMinutes
    );

    ResponseEntity<ApiResponse<CourseResponse>> getByIdResponse(UUID id);

    ResponseEntity<ApiResponse<CourseResponse>> createCourse(@Valid CourseCreateRequest request);

    ResponseEntity<ApiResponse<CourseResponse>> updateCourse(UUID id, @Valid CourseUpdateRequest request);

    ResponseEntity<ApiResponse<Void>> deleteCourse(UUID id);
}
