package uz.mirmaxsudov.lmsbackend.service.base.lms;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.LessonSession;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.LessonSessionStatus;
import uz.mirmaxsudov.lmsbackend.model.request.lms.LessonSessionCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.LessonSessionGenerateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.LessonSessionUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.LessonSessionResponse;
import uz.mirmaxsudov.lmsbackend.service.base.BaseCRUDService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface LessonSessionService extends BaseCRUDService<LessonSession> {
    ResponseEntity<ApiPaginateResponse<List<LessonSessionResponse>>> getAll(
            int page,
            int size,
            UUID groupId,
            UUID lessonId,
            LessonSessionStatus status,
            LocalDateTime from,
            LocalDateTime to
    );

    ResponseEntity<ApiResponse<LessonSessionResponse>> getByIdResponse(UUID id);

    ResponseEntity<ApiResponse<LessonSessionResponse>> createLessonSession(@Valid LessonSessionCreateRequest request);

    ResponseEntity<ApiResponse<LessonSessionResponse>> updateLessonSession(UUID id, @Valid LessonSessionUpdateRequest request);

    ResponseEntity<ApiResponse<List<LessonSessionResponse>>> generateForGroup(
            UUID groupId,
            @Valid LessonSessionGenerateRequest request
    );

    ResponseEntity<ApiResponse<Void>> deleteLessonSession(UUID id);
}
