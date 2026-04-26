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
import uz.mirmaxsudov.lmsbackend.model.request.lms.CourseSectionCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.CourseSectionUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.CourseSectionResponse;
import uz.mirmaxsudov.lmsbackend.service.base.lms.CourseSectionService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(APIUtil.API_BASE_URL + "course-sections")
public class CourseSectionController {

    private final CourseSectionService courseSectionService;

    @GetMapping
    public ResponseEntity<ApiPaginateResponse<List<CourseSectionResponse>>> getAll(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "courseId", required = false) UUID courseId,
            @RequestParam(value = "orderIndex", required = false) Integer orderIndex
    ) {
        return courseSectionService.getAll(page, size, search, courseId, orderIndex);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseSectionResponse>> getById(@PathVariable UUID id) {
        return courseSectionService.getByIdResponse(id);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CourseSectionResponse>> create(@RequestBody @Valid CourseSectionCreateRequest request) {
        return courseSectionService.createSection(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseSectionResponse>> update(
            @PathVariable UUID id,
            @RequestBody @Valid CourseSectionUpdateRequest request
    ) {
        return courseSectionService.updateSection(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        return courseSectionService.deleteSection(id);
    }
}
