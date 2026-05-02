package uz.mirmaxsudov.lmsbackend.service.impl.lms;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.mirmaxsudov.lmsbackend.common.filter.PageableBuilder;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.AttendanceNoteMapper;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomBadRequestException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomConflictException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomNotFoundException;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Attendance;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.AttendanceNote;
import uz.mirmaxsudov.lmsbackend.model.request.lms.AttendanceNoteCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.AttendanceNoteUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.AttendanceNoteResponse;
import uz.mirmaxsudov.lmsbackend.repository.lms.attendance.AttendanceRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.attendanceNote.AttendanceNoteFilter;
import uz.mirmaxsudov.lmsbackend.repository.lms.attendanceNote.AttendanceNoteRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.attendanceNote.AttendanceNoteSpecification;
import uz.mirmaxsudov.lmsbackend.service.base.lms.AttendanceNoteService;
import uz.mirmaxsudov.lmsbackend.service.impl.BaseCRUDServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AttendanceNoteServiceImpl extends BaseCRUDServiceImpl<AttendanceNote, AttendanceNoteRepository> implements AttendanceNoteService {
    private final AttendanceRepository attendanceRepository;

    public AttendanceNoteServiceImpl(
            AttendanceNoteRepository repository,
            AttendanceRepository attendanceRepository
    ) {
        super(repository);
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    public ResponseEntity<ApiPaginateResponse<List<AttendanceNoteResponse>>> getAll(
            int page,
            int size,
            UUID attendanceId,
            UUID studentId,
            UUID lessonSessionId,
            UUID groupId,
            UUID lessonId
    ) {
        int normalizedPage = Math.max(page - 1, 0);
        int normalizedSize = size <= 0 ? 10 : size;

        Pageable pageable = PageableBuilder.build(normalizedPage, normalizedSize);
        Specification<AttendanceNote> filter = AttendanceNoteSpecification.filter(AttendanceNoteFilter.builder()
                .attendanceId(attendanceId)
                .studentId(studentId)
                .lessonSessionId(lessonSessionId)
                .groupId(groupId)
                .lessonId(lessonId)
                .build());

        Page<AttendanceNote> attendanceNotes = repository.findAll(filter, pageable);
        List<AttendanceNoteResponse> results = attendanceNotes.getContent().stream()
                .map(AttendanceNoteMapper::toResponse)
                .toList();

        return ResponseEntity.ok(ApiPaginateResponse.<List<AttendanceNoteResponse>>builder()
                .success(true)
                .message("Attendance notes fetched successfully")
                .results(results)
                .total((int) attendanceNotes.getTotalElements())
                .page(attendanceNotes.getNumber() + 1)
                .size(attendanceNotes.getSize())
                .hasNext(attendanceNotes.hasNext())
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<AttendanceNoteResponse>> getByIdResponse(UUID id) {
        AttendanceNote attendanceNote = findActiveAttendanceNote(id);

        return ResponseEntity.ok(ApiResponse.<AttendanceNoteResponse>builder()
                .success(true)
                .message("Attendance note fetched successfully")
                .data(AttendanceNoteMapper.toResponse(attendanceNote))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<AttendanceNoteResponse>> createAttendanceNote(AttendanceNoteCreateRequest request) {
        Attendance attendance = findActiveAttendance(request.getAttendanceId());
        String normalizedNote = normalizeNote(request.getNote());

        AttendanceNote attendanceNote = repository.findByAttendanceId(attendance.getId())
                .map(existingNote -> {
                    if (!existingNote.isDeleted())
                        throw new CustomConflictException("Attendance note already exists for this attendance");

                    existingNote.setDeleted(false);
                    existingNote.setDeletedAt(null);
                    existingNote.setAttendance(attendance);
                    existingNote.setNote(normalizedNote);
                    return existingNote;
                })
                .orElseGet(() -> AttendanceNote.builder()
                        .attendance(attendance)
                        .note(normalizedNote)
                        .build());

        AttendanceNote savedAttendanceNote = repository.save(attendanceNote);

        return ResponseEntity.ok(ApiResponse.<AttendanceNoteResponse>builder()
                .success(true)
                .message("Attendance note created successfully")
                .data(AttendanceNoteMapper.toResponse(savedAttendanceNote))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<AttendanceNoteResponse>> updateAttendanceNote(UUID id, AttendanceNoteUpdateRequest request) {
        AttendanceNote existingAttendanceNote = findActiveAttendanceNote(id);
        Attendance attendance = findActiveAttendance(request.getAttendanceId());

        validateDuplicate(attendance.getId(), id);

        existingAttendanceNote.setAttendance(attendance);
        existingAttendanceNote.setNote(normalizeNote(request.getNote()));

        AttendanceNote updatedAttendanceNote = repository.save(existingAttendanceNote);

        return ResponseEntity.ok(ApiResponse.<AttendanceNoteResponse>builder()
                .success(true)
                .message("Attendance note updated successfully")
                .data(AttendanceNoteMapper.toResponse(updatedAttendanceNote))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteAttendanceNote(UUID id) {
        AttendanceNote existingAttendanceNote = findActiveAttendanceNote(id);

        existingAttendanceNote.setDeleted(true);
        existingAttendanceNote.setDeletedAt(LocalDateTime.now());
        repository.save(existingAttendanceNote);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Attendance note deleted successfully")
                .build());
    }

    private AttendanceNote findActiveAttendanceNote(UUID id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new CustomNotFoundException("Attendance note not found with id: " + id));
    }

    private Attendance findActiveAttendance(UUID attendanceId) {
        return attendanceRepository.findByIdAndDeletedFalse(attendanceId)
                .orElseThrow(() -> new CustomNotFoundException("Attendance not found with id: " + attendanceId));
    }

    private String normalizeNote(String note) {
        if (note == null)
            throw new CustomBadRequestException("Note is required");

        String trimmed = note.trim();
        if (trimmed.isBlank())
            throw new CustomBadRequestException("Note is required");

        if (trimmed.length() > 1000)
            throw new CustomBadRequestException("Note must be less than or equal to 1000 characters");

        return trimmed;
    }

    private void validateDuplicate(UUID attendanceId, UUID excludedAttendanceNoteId) {
        if (repository.existsByAttendanceIdAndIdNotAndDeletedFalse(attendanceId, excludedAttendanceNoteId))
            throw new CustomConflictException("Attendance note already exists for this attendance");
    }
}
