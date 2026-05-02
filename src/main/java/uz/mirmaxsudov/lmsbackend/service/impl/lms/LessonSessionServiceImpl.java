package uz.mirmaxsudov.lmsbackend.service.impl.lms;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.mirmaxsudov.lmsbackend.common.filter.PageableBuilder;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.LessonSessionMapper;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomBadRequestException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomConflictException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomNotFoundException;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Group;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Lesson;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.LessonSession;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Schedule;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.GroupStatus;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.LessonSessionStatus;
import uz.mirmaxsudov.lmsbackend.model.request.lms.LessonSessionCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.LessonSessionGenerateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.LessonSessionUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.LessonSessionResponse;
import uz.mirmaxsudov.lmsbackend.repository.lms.group.GroupRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.lesson.LessonRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.lessonSession.LessonSessionFilter;
import uz.mirmaxsudov.lmsbackend.repository.lms.lessonSession.LessonSessionRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.lessonSession.LessonSessionSpecification;
import uz.mirmaxsudov.lmsbackend.repository.lms.schedule.ScheduleRepository;
import uz.mirmaxsudov.lmsbackend.service.base.lms.LessonSessionService;
import uz.mirmaxsudov.lmsbackend.service.impl.BaseCRUDServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class LessonSessionServiceImpl extends BaseCRUDServiceImpl<LessonSession, LessonSessionRepository> implements LessonSessionService {
    private final GroupRepository groupRepository;
    private final LessonRepository lessonRepository;
    private final ScheduleRepository scheduleRepository;

    public LessonSessionServiceImpl(
            LessonSessionRepository repository,
            GroupRepository groupRepository,
            LessonRepository lessonRepository,
            ScheduleRepository scheduleRepository
    ) {
        super(repository);
        this.groupRepository = groupRepository;
        this.lessonRepository = lessonRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public ResponseEntity<ApiPaginateResponse<List<LessonSessionResponse>>> getAll(
            int page,
            int size,
            UUID groupId,
            UUID lessonId,
            LessonSessionStatus status,
            LocalDateTime from,
            LocalDateTime to
    ) {
        int normalizedPage = Math.max(page - 1, 0);
        int normalizedSize = size <= 0 ? 10 : size;
        validateDateTimeRange(from, to);

        Pageable pageable = PageableBuilder.build(normalizedPage, normalizedSize);
        Specification<LessonSession> filter = LessonSessionSpecification.filter(LessonSessionFilter.builder()
                .groupId(groupId)
                .lessonId(lessonId)
                .status(status)
                .from(from)
                .to(to)
                .build());

        Page<LessonSession> lessonSessions = repository.findAll(filter, pageable);
        List<LessonSessionResponse> results = lessonSessions.getContent().stream()
                .map(LessonSessionMapper::toResponse)
                .toList();

        return ResponseEntity.ok(ApiPaginateResponse.<List<LessonSessionResponse>>builder()
                .success(true)
                .message("Lesson sessions fetched successfully")
                .results(results)
                .total((int) lessonSessions.getTotalElements())
                .page(lessonSessions.getNumber() + 1)
                .size(lessonSessions.getSize())
                .hasNext(lessonSessions.hasNext())
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<LessonSessionResponse>> getByIdResponse(UUID id) {
        LessonSession lessonSession = findActiveLessonSession(id);

        return ResponseEntity.ok(ApiResponse.<LessonSessionResponse>builder()
                .success(true)
                .message("Lesson session fetched successfully")
                .data(LessonSessionMapper.toResponse(lessonSession))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<LessonSessionResponse>> createLessonSession(LessonSessionCreateRequest request) {
        Group group = findActiveGroup(request.getGroupId());
        Lesson lesson = findActiveLesson(request.getLessonId());

        validateGroupActive(group);
        validateLessonBelongsToGroupCourse(group, lesson);
        validateTimeRange(request.getStartTime(), request.getEndTime());
        validateDuplicate(group.getId(), lesson.getId(), request.getStartTime(), null);

        LessonSession lessonSession = LessonSession.builder()
                .group(group)
                .lesson(lesson)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(request.getStatus() == null ? LessonSessionStatus.PLANNED : request.getStatus())
                .build();

        LessonSession savedLessonSession = repository.save(lessonSession);

        return ResponseEntity.ok(ApiResponse.<LessonSessionResponse>builder()
                .success(true)
                .message("Lesson session created successfully")
                .data(LessonSessionMapper.toResponse(savedLessonSession))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<LessonSessionResponse>> updateLessonSession(UUID id, LessonSessionUpdateRequest request) {
        LessonSession existingLessonSession = findActiveLessonSession(id);
        Group group = findActiveGroup(request.getGroupId());
        Lesson lesson = findActiveLesson(request.getLessonId());

        validateGroupActive(group);
        validateLessonBelongsToGroupCourse(group, lesson);
        validateTimeRange(request.getStartTime(), request.getEndTime());
        validateDuplicate(group.getId(), lesson.getId(), request.getStartTime(), id);

        existingLessonSession.setGroup(group);
        existingLessonSession.setLesson(lesson);
        existingLessonSession.setStartTime(request.getStartTime());
        existingLessonSession.setEndTime(request.getEndTime());
        existingLessonSession.setStatus(request.getStatus());

        LessonSession updatedLessonSession = repository.save(existingLessonSession);

        return ResponseEntity.ok(ApiResponse.<LessonSessionResponse>builder()
                .success(true)
                .message("Lesson session updated successfully")
                .data(LessonSessionMapper.toResponse(updatedLessonSession))
                .build());
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<List<LessonSessionResponse>>> generateForGroup(
            UUID groupId,
            LessonSessionGenerateRequest request
    ) {
        Group group = findActiveGroup(groupId);
        validateGroupActive(group);
        validateDateRange(request.getFromDate(), request.getToDate());

        List<Schedule> schedules = scheduleRepository.findAllByGroupIdAndDeletedFalse(group.getId()).stream()
                .sorted(Comparator.comparing(Schedule::getDayOfWeek).thenComparing(Schedule::getStartTime))
                .toList();
        if (schedules.isEmpty())
            throw new CustomBadRequestException("Group has no schedules");

        List<Lesson> lessons = lessonRepository.findActiveByCourseIdOrderBySectionAndCreatedAt(group.getCourse().getId());
        if (lessons.isEmpty())
            throw new CustomBadRequestException("Group course has no lessons");

        Set<UUID> alreadyScheduledLessonIds = repository.findAllByGroupIdAndDeletedFalseOrderByStartTimeAsc(group.getId()).stream()
                .map(LessonSession::getLesson)
                .filter(lesson -> lesson != null && lesson.getId() != null)
                .map(Lesson::getId)
                .collect(java.util.stream.Collectors.toSet());

        List<Lesson> unscheduledLessons = lessons.stream()
                .filter(lesson -> !alreadyScheduledLessonIds.contains(lesson.getId()))
                .toList();
        if (unscheduledLessons.isEmpty())
            throw new CustomBadRequestException("All lessons are already scheduled for this group");

        List<SessionSlot> slots = buildSlots(schedules, request.getFromDate(), request.getToDate()).stream()
                .filter(slot -> !repository.existsByGroupIdAndStartTimeAndDeletedFalse(group.getId(), slot.startTime()))
                .toList();
        if (slots.isEmpty())
            throw new CustomBadRequestException("No available schedule slots found in the selected date range");

        int count = Math.min(unscheduledLessons.size(), slots.size());
        List<LessonSession> generatedSessions = new java.util.ArrayList<>();
        for (int index = 0; index < count; index++) {
            Lesson lesson = unscheduledLessons.get(index);
            SessionSlot slot = slots.get(index);

            generatedSessions.add(LessonSession.builder()
                    .group(group)
                    .lesson(lesson)
                    .startTime(slot.startTime())
                    .endTime(slot.endTime())
                    .status(LessonSessionStatus.PLANNED)
                    .build());
        }

        List<LessonSessionResponse> responses = repository.saveAll(generatedSessions).stream()
                .map(LessonSessionMapper::toResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.<List<LessonSessionResponse>>builder()
                .success(true)
                .message("Lesson sessions generated successfully")
                .data(responses)
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteLessonSession(UUID id) {
        LessonSession existingLessonSession = findActiveLessonSession(id);

        existingLessonSession.setDeleted(true);
        existingLessonSession.setDeletedAt(LocalDateTime.now());
        repository.save(existingLessonSession);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Lesson session deleted successfully")
                .build());
    }

    private LessonSession findActiveLessonSession(UUID id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new CustomNotFoundException("Lesson session not found with id: " + id));
    }

    private Group findActiveGroup(UUID groupId) {
        return groupRepository.findByIdAndDeletedFalse(groupId)
                .orElseThrow(() -> new CustomNotFoundException("Group not found with id: " + groupId));
    }

    private Lesson findActiveLesson(UUID lessonId) {
        return lessonRepository.findByIdAndDeletedFalse(lessonId)
                .orElseThrow(() -> new CustomNotFoundException("Lesson not found with id: " + lessonId));
    }

    private void validateGroupActive(Group group) {
        if (group.getStatus() != GroupStatus.ACTIVE)
            throw new CustomBadRequestException("Lesson sessions can be created only for active groups");
    }

    private void validateLessonBelongsToGroupCourse(Group group, Lesson lesson) {
        UUID groupCourseId = group.getCourse() == null ? null : group.getCourse().getId();
        UUID lessonCourseId = lesson.getSection() == null || lesson.getSection().getCourse() == null
                ? null
                : lesson.getSection().getCourse().getId();

        if (groupCourseId == null || !groupCourseId.equals(lessonCourseId))
            throw new CustomBadRequestException("Lesson must belong to the group's course");
    }

    private void validateTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null)
            throw new CustomBadRequestException("Start time is required");

        if (endTime == null)
            throw new CustomBadRequestException("End time is required");

        if (!endTime.isAfter(startTime))
            throw new CustomBadRequestException("Lesson session end time must be after start time");
    }

    private void validateDateTimeRange(LocalDateTime from, LocalDateTime to) {
        if (from != null && to != null && from.isAfter(to))
            throw new CustomBadRequestException("from must be before or equal to to");
    }

    private void validateDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null)
            throw new CustomBadRequestException("From date is required");

        if (toDate == null)
            throw new CustomBadRequestException("To date is required");

        if (fromDate.isAfter(toDate))
            throw new CustomBadRequestException("fromDate must be before or equal to toDate");
    }

    private void validateDuplicate(UUID groupId, UUID lessonId, LocalDateTime startTime, UUID excludedLessonSessionId) {
        boolean exists = excludedLessonSessionId == null
                ? repository.existsByGroupIdAndLessonIdAndStartTimeAndDeletedFalse(groupId, lessonId, startTime)
                : repository.existsByGroupIdAndLessonIdAndStartTimeAndIdNotAndDeletedFalse(
                groupId,
                lessonId,
                startTime,
                excludedLessonSessionId
        );

        if (exists)
            throw new CustomConflictException("Lesson session already exists for this group, lesson, and start time");
    }

    private List<SessionSlot> buildSlots(List<Schedule> schedules, LocalDate fromDate, LocalDate toDate) {
        List<SessionSlot> slots = new java.util.ArrayList<>();
        LocalDate current = fromDate;

        while (!current.isAfter(toDate)) {
            for (Schedule schedule : schedules) {
                if (schedule.getDayOfWeek() == current.getDayOfWeek()) {
                    slots.add(new SessionSlot(
                            LocalDateTime.of(current, schedule.getStartTime()),
                            LocalDateTime.of(current, schedule.getEndTime())
                    ));
                }
            }

            current = current.plusDays(1);
        }

        return slots.stream()
                .sorted(Comparator.comparing(SessionSlot::startTime))
                .toList();
    }

    private record SessionSlot(LocalDateTime startTime, LocalDateTime endTime) {
    }
}
