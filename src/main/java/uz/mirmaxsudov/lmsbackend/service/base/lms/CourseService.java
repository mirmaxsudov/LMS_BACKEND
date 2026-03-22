package uz.mirmaxsudov.lmsbackend.service.base.lms;

import org.springframework.http.ResponseEntity;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.request.lms.CourseCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.CourseUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.lms.CourseResponse;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;

import java.util.List;
import java.util.UUID;

public interface CourseService {
    ResponseEntity<ApiResponse<CourseResponse>> create(CourseCreateRequest request, CustomUserDetails details);

    ResponseEntity<ApiResponse<CourseResponse>> getById(UUID id, CustomUserDetails details);

    ResponseEntity<ApiResponse<CourseResponse>> update(UUID id, CourseUpdateRequest request, CustomUserDetails details);

    ResponseEntity<ApiResponse<Void>> delete(UUID id, CustomUserDetails details);

    ResponseEntity<ApiPaginateResponse<List<CourseResponse>>> getPaginate(int page, int size, String search, UUID teacherId, Boolean active, CustomUserDetails details);
}
