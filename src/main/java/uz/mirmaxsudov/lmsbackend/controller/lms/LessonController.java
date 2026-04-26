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
import uz.mirmaxsudov.lmsbackend.model.request.lms.LessonCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.LessonUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.LessonResponse;
import uz.mirmaxsudov.lmsbackend.service.base.lms.LessonService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(APIUtil.API_BASE_URL + "lessons")
public class LessonController {

    private final LessonService lessonService;

    @GetMapping
    public ResponseEntity<ApiPaginateResponse<List<LessonResponse>>> getAll(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sectionId", required = false) UUID sectionId,
            @RequestParam(value = "minDuration", required = false) Integer minDuration,
            @RequestParam(value = "maxDuration", required = false) Integer maxDuration
    ) {
        return lessonService.getAll(page, size, search, sectionId, minDuration, maxDuration);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LessonResponse>> getById(@PathVariable UUID id) {
        return lessonService.getByIdResponse(id);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LessonResponse>> create(@RequestBody @Valid LessonCreateRequest request) {
        return lessonService.createLesson(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LessonResponse>> update(
            @PathVariable UUID id,
            @RequestBody @Valid LessonUpdateRequest request
    ) {
        return lessonService.updateLesson(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        return lessonService.deleteLesson(id);
    }
}
