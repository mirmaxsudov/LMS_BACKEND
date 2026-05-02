package uz.mirmaxsudov.lmsbackend.controller.lms;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.mirmaxsudov.lmsbackend.common.util.APIUtil;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.LessonSessionStatus;
import uz.mirmaxsudov.lmsbackend.model.request.lms.LessonSessionCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.LessonSessionUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.LessonSessionResponse;
import uz.mirmaxsudov.lmsbackend.service.base.lms.LessonSessionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(APIUtil.API_BASE_URL + "lesson-sessions")
public class LessonSessionController {
    private final LessonSessionService lessonSessionService;

    @GetMapping
    public ResponseEntity<ApiPaginateResponse<List<LessonSessionResponse>>> getAll(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "groupId", required = false) UUID groupId,
            @RequestParam(value = "lessonId", required = false) UUID lessonId,
            @RequestParam(value = "status", required = false) LessonSessionStatus status,
            @RequestParam(value = "from", required = false) LocalDateTime from,
            @RequestParam(value = "to", required = false) LocalDateTime to
    ) {
        return lessonSessionService.getAll(page, size, groupId, lessonId, status, from, to);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LessonSessionResponse>> getById(@PathVariable UUID id) {
        return lessonSessionService.getByIdResponse(id);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LessonSessionResponse>> create(
            @RequestBody @Valid LessonSessionCreateRequest request
    ) {
        return lessonSessionService.createLessonSession(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LessonSessionResponse>> update(
            @PathVariable UUID id,
            @RequestBody @Valid LessonSessionUpdateRequest request
    ) {
        return lessonSessionService.updateLessonSession(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        return lessonSessionService.deleteLessonSession(id);
    }
}
