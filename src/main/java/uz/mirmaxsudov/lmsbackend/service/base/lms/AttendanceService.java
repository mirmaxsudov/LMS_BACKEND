package uz.mirmaxsudov.lmsbackend.service.base.lms;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Attendance;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.AttendanceStatus;
import uz.mirmaxsudov.lmsbackend.model.request.lms.AttendanceCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.AttendanceUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.AttendanceResponse;
import uz.mirmaxsudov.lmsbackend.service.base.BaseCRUDService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AttendanceService extends BaseCRUDService<Attendance> {
    ResponseEntity<ApiPaginateResponse<List<AttendanceResponse>>> getAll(
            int page,
            int size,
            UUID studentId,
            UUID lessonSessionId,
            UUID groupId,
            UUID lessonId,
            AttendanceStatus status,
            LocalDateTime from,
            LocalDateTime to
    );

    ResponseEntity<ApiResponse<AttendanceResponse>> getByIdResponse(UUID id);

    ResponseEntity<ApiResponse<AttendanceResponse>> createAttendance(@Valid AttendanceCreateRequest request);

    ResponseEntity<ApiResponse<AttendanceResponse>> updateAttendance(UUID id, @Valid AttendanceUpdateRequest request);

    ResponseEntity<ApiResponse<Void>> deleteAttendance(UUID id);
}
