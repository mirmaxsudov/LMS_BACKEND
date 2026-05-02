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
import uz.mirmaxsudov.lmsbackend.model.request.lms.AttendanceNoteCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.AttendanceNoteUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.AttendanceNoteResponse;
import uz.mirmaxsudov.lmsbackend.service.base.lms.AttendanceNoteService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(APIUtil.API_BASE_URL + "attendance-notes")
public class AttendanceNoteController {
    private final AttendanceNoteService attendanceNoteService;

    @GetMapping
    public ResponseEntity<ApiPaginateResponse<List<AttendanceNoteResponse>>> getAll(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "attendanceId", required = false) UUID attendanceId,
            @RequestParam(value = "studentId", required = false) UUID studentId,
            @RequestParam(value = "lessonSessionId", required = false) UUID lessonSessionId,
            @RequestParam(value = "groupId", required = false) UUID groupId,
            @RequestParam(value = "lessonId", required = false) UUID lessonId
    ) {
        return attendanceNoteService.getAll(page, size, attendanceId, studentId, lessonSessionId, groupId, lessonId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AttendanceNoteResponse>> getById(@PathVariable UUID id) {
        return attendanceNoteService.getByIdResponse(id);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AttendanceNoteResponse>> create(
            @RequestBody @Valid AttendanceNoteCreateRequest request
    ) {
        return attendanceNoteService.createAttendanceNote(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AttendanceNoteResponse>> update(
            @PathVariable UUID id,
            @RequestBody @Valid AttendanceNoteUpdateRequest request
    ) {
        return attendanceNoteService.updateAttendanceNote(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        return attendanceNoteService.deleteAttendanceNote(id);
    }
}
