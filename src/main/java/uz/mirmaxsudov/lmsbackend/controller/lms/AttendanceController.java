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
import uz.mirmaxsudov.lmsbackend.model.enums.lms.AttendanceStatus;
import uz.mirmaxsudov.lmsbackend.model.request.lms.AttendanceCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.AttendanceUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.AttendanceResponse;
import uz.mirmaxsudov.lmsbackend.service.base.lms.AttendanceService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(APIUtil.API_BASE_URL + "attendances")
public class AttendanceController {
    private final AttendanceService attendanceService;

    @GetMapping
    public ResponseEntity<ApiPaginateResponse<List<AttendanceResponse>>> getAll(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "studentId", required = false) UUID studentId,
            @RequestParam(value = "lessonSessionId", required = false) UUID lessonSessionId,
            @RequestParam(value = "groupId", required = false) UUID groupId,
            @RequestParam(value = "lessonId", required = false) UUID lessonId,
            @RequestParam(value = "status", required = false) AttendanceStatus status,
            @RequestParam(value = "from", required = false) LocalDateTime from,
            @RequestParam(value = "to", required = false) LocalDateTime to
    ) {
        return attendanceService.getAll(page, size, studentId, lessonSessionId, groupId, lessonId, status, from, to);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AttendanceResponse>> getById(@PathVariable UUID id) {
        return attendanceService.getByIdResponse(id);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AttendanceResponse>> create(@RequestBody @Valid AttendanceCreateRequest request) {
        return attendanceService.createAttendance(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AttendanceResponse>> update(
            @PathVariable UUID id,
            @RequestBody @Valid AttendanceUpdateRequest request
    ) {
        return attendanceService.updateAttendance(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        return attendanceService.deleteAttendance(id);
    }
}
