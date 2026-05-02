package uz.mirmaxsudov.lmsbackend.service.base.lms;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.AttendanceNote;
import uz.mirmaxsudov.lmsbackend.model.request.lms.AttendanceNoteCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.AttendanceNoteUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.AttendanceNoteResponse;
import uz.mirmaxsudov.lmsbackend.service.base.BaseCRUDService;

import java.util.List;
import java.util.UUID;

public interface AttendanceNoteService extends BaseCRUDService<AttendanceNote> {
    ResponseEntity<ApiPaginateResponse<List<AttendanceNoteResponse>>> getAll(
            int page,
            int size,
            UUID attendanceId,
            UUID studentId,
            UUID lessonSessionId,
            UUID groupId,
            UUID lessonId
    );

    ResponseEntity<ApiResponse<AttendanceNoteResponse>> getByIdResponse(UUID id);

    ResponseEntity<ApiResponse<AttendanceNoteResponse>> createAttendanceNote(@Valid AttendanceNoteCreateRequest request);

    ResponseEntity<ApiResponse<AttendanceNoteResponse>> updateAttendanceNote(UUID id, @Valid AttendanceNoteUpdateRequest request);

    ResponseEntity<ApiResponse<Void>> deleteAttendanceNote(UUID id);
}
