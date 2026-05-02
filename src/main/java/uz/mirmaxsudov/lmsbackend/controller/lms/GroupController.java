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
import uz.mirmaxsudov.lmsbackend.model.enums.lms.GroupStatus;
import uz.mirmaxsudov.lmsbackend.model.request.lms.GroupCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.GroupStartRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.GroupUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.LessonSessionGenerateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.GroupResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.GroupStartResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.LessonSessionResponse;
import uz.mirmaxsudov.lmsbackend.service.base.lms.GroupService;
import uz.mirmaxsudov.lmsbackend.service.base.lms.LessonSessionService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(APIUtil.API_BASE_URL + "groups")
public class GroupController {
    private final GroupService groupService;
    private final LessonSessionService lessonSessionService;

    @GetMapping
    public ResponseEntity<ApiPaginateResponse<List<GroupResponse>>> getAll(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "courseId", required = false) UUID courseId,
            @RequestParam(value = "teacherId", required = false) UUID teacherId,
            @RequestParam(value = "status", required = false) GroupStatus status,
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "minCapacity", required = false) Integer minCapacity,
            @RequestParam(value = "maxCapacity", required = false) Integer maxCapacity
    ) {
        return groupService.getAll(page, size, search, courseId, teacherId, status, active, minCapacity, maxCapacity);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GroupResponse>> getById(@PathVariable UUID id) {
        return groupService.getByIdResponse(id);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GroupResponse>> create(@RequestBody @Valid GroupCreateRequest request) {
        return groupService.createGroup(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GroupResponse>> update(
            @PathVariable UUID id,
            @RequestBody @Valid GroupUpdateRequest request
    ) {
        return groupService.updateGroup(id, request);
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<ApiResponse<GroupStartResponse>> start(
            @PathVariable UUID id,
            @RequestBody @Valid GroupStartRequest request
    ) {
        return groupService.startGroup(id, request);
    }

    @PostMapping("/{id}/lesson-sessions/generate")
    public ResponseEntity<ApiResponse<List<LessonSessionResponse>>> generateLessonSessions(
            @PathVariable UUID id,
            @RequestBody @Valid LessonSessionGenerateRequest request
    ) {
        return lessonSessionService.generateForGroup(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        return groupService.deleteGroup(id);
    }
}
