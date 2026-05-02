package uz.mirmaxsudov.lmsbackend.service.impl.lms;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.mirmaxsudov.lmsbackend.common.filter.PageableBuilder;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.AttendanceMapper;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomBadRequestException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomConflictException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomNotFoundException;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Attendance;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Group;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.LessonSession;
import uz.mirmaxsudov.lmsbackend.model.entity.user.StudentProfile;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.AttendanceStatus;
import uz.mirmaxsudov.lmsbackend.model.request.lms.AttendanceCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.AttendanceUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.AttendanceResponse;
import uz.mirmaxsudov.lmsbackend.repository.lms.attendance.AttendanceFilter;
import uz.mirmaxsudov.lmsbackend.repository.lms.attendance.AttendanceRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.attendance.AttendanceSpecification;
import uz.mirmaxsudov.lmsbackend.repository.lms.lessonSession.LessonSessionRepository;
import uz.mirmaxsudov.lmsbackend.repository.user.StudentProfileRepository;
import uz.mirmaxsudov.lmsbackend.service.base.lms.AttendanceService;
import uz.mirmaxsudov.lmsbackend.service.impl.BaseCRUDServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AttendanceServiceImpl extends BaseCRUDServiceImpl<Attendance, AttendanceRepository> implements AttendanceService {
    private final StudentProfileRepository studentProfileRepository;
    private final LessonSessionRepository lessonSessionRepository;

    public AttendanceServiceImpl(
            AttendanceRepository repository,
            StudentProfileRepository studentProfileRepository,
            LessonSessionRepository lessonSessionRepository
    ) {
        super(repository);
        this.studentProfileRepository = studentProfileRepository;
        this.lessonSessionRepository = lessonSessionRepository;
    }

    @Override
    public ResponseEntity<ApiPaginateResponse<List<AttendanceResponse>>> getAll(
            int page,
            int size,
            UUID studentId,
            UUID lessonSessionId,
            UUID groupId,
            UUID lessonId,
            AttendanceStatus status,
            LocalDateTime from,
            LocalDateTime to
    ) {
        int normalizedPage = Math.max(page - 1, 0);
        int normalizedSize = size <= 0 ? 10 : size;
        validateDateTimeRange(from, to);

        Pageable pageable = PageableBuilder.build(normalizedPage, normalizedSize);
        Specification<Attendance> filter = AttendanceSpecification.filter(AttendanceFilter.builder()
                .studentId(studentId)
                .lessonSessionId(lessonSessionId)
                .groupId(groupId)
                .lessonId(lessonId)
                .status(status)
                .from(from)
                .to(to)
                .build());

        Page<Attendance> attendances = repository.findAll(filter, pageable);
        List<AttendanceResponse> results = attendances.getContent().stream()
                .map(AttendanceMapper::toResponse)
                .toList();

        return ResponseEntity.ok(ApiPaginateResponse.<List<AttendanceResponse>>builder()
                .success(true)
                .message("Attendances fetched successfully")
                .results(results)
                .total((int) attendances.getTotalElements())
                .page(attendances.getNumber() + 1)
                .size(attendances.getSize())
                .hasNext(attendances.hasNext())
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<AttendanceResponse>> getByIdResponse(UUID id) {
        Attendance attendance = findActiveAttendance(id);

        return ResponseEntity.ok(ApiResponse.<AttendanceResponse>builder()
                .success(true)
                .message("Attendance fetched successfully")
                .data(AttendanceMapper.toResponse(attendance))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<AttendanceResponse>> createAttendance(AttendanceCreateRequest request) {
        StudentProfile student = findActiveStudent(request.getStudentId());
        LessonSession lessonSession = findActiveLessonSession(request.getLessonSessionId());

        validateStudentBelongsToSessionGroup(student, lessonSession);
        validateDuplicate(student.getId(), lessonSession.getId(), null);

        Attendance attendance = Attendance.builder()
                .student(student)
                .lessonSession(lessonSession)
                .status(request.getStatus())
                .build();

        Attendance savedAttendance = repository.save(attendance);

        return ResponseEntity.ok(ApiResponse.<AttendanceResponse>builder()
                .success(true)
                .message("Attendance created successfully")
                .data(AttendanceMapper.toResponse(savedAttendance))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<AttendanceResponse>> updateAttendance(UUID id, AttendanceUpdateRequest request) {
        Attendance existingAttendance = findActiveAttendance(id);
        StudentProfile student = findActiveStudent(request.getStudentId());
        LessonSession lessonSession = findActiveLessonSession(request.getLessonSessionId());

        validateStudentBelongsToSessionGroup(student, lessonSession);
        validateDuplicate(student.getId(), lessonSession.getId(), id);

        existingAttendance.setStudent(student);
        existingAttendance.setLessonSession(lessonSession);
        existingAttendance.setStatus(request.getStatus());

        Attendance updatedAttendance = repository.save(existingAttendance);

        return ResponseEntity.ok(ApiResponse.<AttendanceResponse>builder()
                .success(true)
                .message("Attendance updated successfully")
                .data(AttendanceMapper.toResponse(updatedAttendance))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteAttendance(UUID id) {
        Attendance existingAttendance = findActiveAttendance(id);

        existingAttendance.setDeleted(true);
        existingAttendance.setDeletedAt(LocalDateTime.now());
        repository.save(existingAttendance);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Attendance deleted successfully")
                .build());
    }

    private Attendance findActiveAttendance(UUID id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new CustomNotFoundException("Attendance not found with id: " + id));
    }

    private StudentProfile findActiveStudent(UUID studentId) {
        return studentProfileRepository.findByIdAndDeletedFalse(studentId)
                .orElseThrow(() -> new CustomNotFoundException("Student profile not found with id: " + studentId));
    }

    private LessonSession findActiveLessonSession(UUID lessonSessionId) {
        return lessonSessionRepository.findByIdAndDeletedFalse(lessonSessionId)
                .orElseThrow(() -> new CustomNotFoundException("Lesson session not found with id: " + lessonSessionId));
    }

    private void validateStudentBelongsToSessionGroup(StudentProfile student, LessonSession lessonSession) {
        Group group = lessonSession.getGroup();
        if (group == null || group.getId() == null)
            throw new CustomBadRequestException("Lesson session group is required");

        if (!studentProfileRepository.existsActiveStudentInGroup(student.getId(), group.getId()))
            throw new CustomBadRequestException("Student does not belong to the lesson session group");
    }

    private void validateDuplicate(UUID studentId, UUID lessonSessionId, UUID excludedAttendanceId) {
        boolean exists = excludedAttendanceId == null
                ? repository.existsByStudentIdAndLessonSessionIdAndDeletedFalse(studentId, lessonSessionId)
                : repository.existsByStudentIdAndLessonSessionIdAndIdNotAndDeletedFalse(
                studentId,
                lessonSessionId,
                excludedAttendanceId
        );

        if (exists)
            throw new CustomConflictException("Attendance already exists for this student and lesson session");
    }

    private void validateDateTimeRange(LocalDateTime from, LocalDateTime to) {
        if (from != null && to != null && from.isAfter(to))
            throw new CustomBadRequestException("from must be before or equal to to");
    }
}
