package uz.mirmaxsudov.lmsbackend.service.base.lms;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Lesson;
import uz.mirmaxsudov.lmsbackend.model.request.lms.LessonCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.LessonUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.LessonResponse;
import uz.mirmaxsudov.lmsbackend.service.base.BaseCRUDService;

import java.util.List;
import java.util.UUID;

public interface LessonService extends BaseCRUDService<Lesson> {
    ResponseEntity<ApiPaginateResponse<List<LessonResponse>>> getAll(
            int page,
            int size,
            String search,
            UUID sectionId,
            Integer minDuration,
            Integer maxDuration
    );

    ResponseEntity<ApiResponse<LessonResponse>> getByIdResponse(UUID id);

    ResponseEntity<ApiResponse<LessonResponse>> createLesson(@Valid LessonCreateRequest request);

    ResponseEntity<ApiResponse<LessonResponse>> updateLesson(UUID id, @Valid LessonUpdateRequest request);

    ResponseEntity<ApiResponse<Void>> deleteLesson(UUID id);
}
