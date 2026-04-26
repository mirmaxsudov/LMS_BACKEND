package uz.mirmaxsudov.lmsbackend.service.base.lms;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.CourseSection;
import uz.mirmaxsudov.lmsbackend.model.request.lms.CourseSectionCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.CourseSectionUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.CourseSectionResponse;
import uz.mirmaxsudov.lmsbackend.service.base.BaseCRUDService;

import java.util.List;
import java.util.UUID;

public interface CourseSectionService extends BaseCRUDService<CourseSection> {
    ResponseEntity<ApiPaginateResponse<List<CourseSectionResponse>>> getAll(
            int page,
            int size,
            String search,
            UUID courseId,
            Integer orderIndex
    );

    ResponseEntity<ApiResponse<CourseSectionResponse>> getByIdResponse(UUID id);

    ResponseEntity<ApiResponse<CourseSectionResponse>> createSection(@Valid CourseSectionCreateRequest request);

    ResponseEntity<ApiResponse<CourseSectionResponse>> updateSection(UUID id, @Valid CourseSectionUpdateRequest request);

    ResponseEntity<ApiResponse<Void>> deleteSection(UUID id);
}
