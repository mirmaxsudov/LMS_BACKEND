package uz.mirmaxsudov.lmsbackend.service.impl.lms;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.mirmaxsudov.lmsbackend.common.filter.PageableBuilder;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.GroupMapper;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.ScheduleMapper;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.TeacherMapper;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomBadRequestException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomConflictException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomNotFoundException;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Course;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Group;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Schedule;
import uz.mirmaxsudov.lmsbackend.model.entity.user.TeacherProfile;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.GroupScheduleType;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.GroupStatus;
import uz.mirmaxsudov.lmsbackend.model.request.lms.GroupCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.GroupScheduleRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.GroupStartRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.GroupUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.GroupResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.GroupStartResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.ScheduleResponse;
import uz.mirmaxsudov.lmsbackend.repository.lms.course.CourseRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.group.GroupFilter;
import uz.mirmaxsudov.lmsbackend.repository.lms.group.GroupRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.group.GroupSpecification;
import uz.mirmaxsudov.lmsbackend.repository.lms.schedule.ScheduleRepository;
import uz.mirmaxsudov.lmsbackend.repository.user.TeacherProfileRepository;
import uz.mirmaxsudov.lmsbackend.service.base.lms.GroupService;
import uz.mirmaxsudov.lmsbackend.service.impl.BaseCRUDServiceImpl;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class GroupServiceImpl extends BaseCRUDServiceImpl<Group, GroupRepository> implements GroupService {
    private final CourseRepository courseRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final ScheduleRepository scheduleRepository;

    public GroupServiceImpl(
            GroupRepository repository,
            CourseRepository courseRepository,
            TeacherProfileRepository teacherProfileRepository,
            ScheduleRepository scheduleRepository
    ) {
        super(repository);
        this.courseRepository = courseRepository;
        this.teacherProfileRepository = teacherProfileRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public ResponseEntity<ApiPaginateResponse<List<GroupResponse>>> getAll(
            int page,
            int size,
            String search,
            UUID courseId,
            UUID teacherId,
            GroupStatus status,
            Boolean active,
            Integer minCapacity,
            Integer maxCapacity
    ) {
        int normalizedPage = Math.max(page - 1, 0);
        int normalizedSize = size <= 0 ? 10 : size;
        validateCapacityRange(minCapacity, maxCapacity);

        Pageable pageable = PageableBuilder.build(normalizedPage, normalizedSize);

        Specification<Group> filter = GroupSpecification.filter(GroupFilter.builder()
                .search(search)
                .courseId(courseId)
                .teacherId(teacherId)
                .status(status)
                .active(active)
                .minCapacity(minCapacity)
                .maxCapacity(maxCapacity)
                .build());

        Page<Group> groups = repository.findAll(filter, pageable);
        List<GroupResponse> results = groups.getContent().stream()
                .map(GroupMapper::toResponse)
                .toList();

        return ResponseEntity.ok(ApiPaginateResponse.<List<GroupResponse>>builder()
                .success(true)
                .message("Groups fetched successfully")
                .results(results)
                .total((int) groups.getTotalElements())
                .page(groups.getNumber() + 1)
                .size(groups.getSize())
                .hasNext(groups.hasNext())
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<GroupResponse>> getByIdResponse(UUID id) {
        Group group = findActiveGroup(id);

        return ResponseEntity.ok(ApiResponse.<GroupResponse>builder()
                .success(true)
                .message("Group fetched successfully")
                .data(GroupMapper.toResponse(group))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<GroupResponse>> createGroup(GroupCreateRequest request) {
        String normalizedName = normalizeName(request.getName());
        Course course = findCourse(request.getCourseId());
        TeacherProfile teacher = findTeacher(request.getTeacherId());

        validateDuplicateName(normalizedName, course.getId(), null);
        validateCapacity(request.getCapacity(), 0);
        Set<DayOfWeek> scheduleDays = normalizeScheduleDays(request.getScheduleType(), request.getScheduleDays());

        Group group = Group.builder()
                .groupName(normalizedName)
                .course(course)
                .teacher(teacher)
                .status(toStatus(request.getActive()))
                .capacity(request.getCapacity())
                .scheduleType(request.getScheduleType())
                .scheduleDays(scheduleDays)
                .build();

        Group savedGroup = repository.save(group);

        return ResponseEntity.ok(ApiResponse.<GroupResponse>builder()
                .success(true)
                .message("Group created successfully")
                .data(GroupMapper.toResponse(savedGroup))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<GroupResponse>> updateGroup(UUID id, GroupUpdateRequest request) {
        Group existingGroup = findActiveGroup(id);

        String normalizedName = normalizeName(request.getName());
        Course course = findCourse(request.getCourseId());
        TeacherProfile teacher = findTeacher(request.getTeacherId());
        int currentStudentsCount = existingGroup.getStudents() == null ? 0 : existingGroup.getStudents().size();

        validateDuplicateName(normalizedName, course.getId(), existingGroup.getId());
        validateCapacity(request.getCapacity(), currentStudentsCount);
        Set<DayOfWeek> scheduleDays = normalizeScheduleDays(request.getScheduleType(), request.getScheduleDays());

        existingGroup.setGroupName(normalizedName);
        existingGroup.setCourse(course);
        existingGroup.setTeacher(teacher);
        existingGroup.setStatus(toStatus(request.getActive()));
        existingGroup.setCapacity(request.getCapacity());
        existingGroup.setScheduleType(request.getScheduleType());
        existingGroup.setScheduleDays(scheduleDays);
        existingGroup.setStatus(request.getStatus());

        Group updatedGroup = repository.save(existingGroup);

        return ResponseEntity.ok(ApiResponse.<GroupResponse>builder()
                .success(true)
                .message("Group updated successfully")
                .data(GroupMapper.toResponse(updatedGroup))
                .build());
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<GroupStartResponse>> startGroup(UUID id, GroupStartRequest request) {
        Group group = findActiveGroup(id);

        validateGroupCanStart(group);
        validateGroupHasNoSchedules(group.getId());

        Set<DayOfWeek> allowedDays = resolveAllowedDays(group);
        validateStartSchedules(request.getSchedules(), allowedDays);

        group.setStatus(GroupStatus.ACTIVE);
        Group savedGroup = repository.save(group);

        List<Schedule> schedules = request.getSchedules().stream()
                .map(scheduleRequest -> Schedule.builder()
                        .group(savedGroup)
                        .dayOfWeek(scheduleRequest.getDayOfWeek())
                        .startTime(scheduleRequest.getStartTime())
                        .endTime(scheduleRequest.getEndTime())
                        .build())
                .toList();

        List<ScheduleResponse> savedSchedules = scheduleRepository.saveAll(schedules).stream()
                .map(ScheduleMapper::toResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.<GroupStartResponse>builder()
                .success(true)
                .message("Group started successfully")
                .data(GroupStartResponse.builder()
                        .group(GroupMapper.toResponse(savedGroup))
                        .schedules(savedSchedules)
                        .build())
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteGroup(UUID id) {
        Group existingGroup = findActiveGroup(id);

        existingGroup.setDeleted(true);
        existingGroup.setDeletedAt(LocalDateTime.now());
        repository.save(existingGroup);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Group deleted successfully")
                .build());
    }

    private Course findCourse(UUID courseId) {
        return courseRepository.findByIdAndDeletedFalse(courseId)
                .orElseThrow(() -> new CustomNotFoundException("Course not found with id: " + courseId));
    }

    private TeacherProfile findTeacher(UUID teacherId) {
        return teacherProfileRepository.findByIdAndDeletedFalse(teacherId)
                .orElseThrow(() -> new CustomNotFoundException("Teacher profile not found with id: " + teacherId));
    }

    private String normalizeName(String name) {
        if (name == null)
            throw new CustomBadRequestException("Group name is required");

        String trimmed = name.trim();
        if (trimmed.isBlank())
            throw new CustomBadRequestException("Group name is required");

        return trimmed;
    }

    private GroupStatus toStatus(Boolean active) {
        return Boolean.FALSE.equals(active) ? GroupStatus.CANCELLED : GroupStatus.ACTIVE;
    }

    private void validateDuplicateName(String groupName, UUID courseId, UUID excludedGroupId) {
        boolean exists = excludedGroupId == null
                ? repository.existsByGroupNameIgnoreCaseAndCourseIdAndDeletedFalse(groupName, courseId)
                : repository.existsByGroupNameIgnoreCaseAndCourseIdAndIdNotAndDeletedFalse(
                groupName,
                courseId,
                excludedGroupId
        );

        if (exists)
            throw new CustomConflictException("Group with this name already exists in the selected course");
    }

    private Group findActiveGroup(UUID id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new CustomNotFoundException("Group not found with id: " + id));
    }

    private void validateCapacity(Integer capacity, int currentStudentsCount) {
        if (capacity == null || capacity <= 0)
            throw new CustomBadRequestException("Capacity must be greater than 0");

        if (capacity < currentStudentsCount)
            throw new CustomBadRequestException(
                    "Capacity cannot be less than current students count: " + currentStudentsCount
            );
    }

    private void validateCapacityRange(Integer minCapacity, Integer maxCapacity) {
        if (minCapacity != null && minCapacity <= 0)
            throw new CustomBadRequestException("minCapacity must be greater than 0");

        if (maxCapacity != null && maxCapacity <= 0)
            throw new CustomBadRequestException("maxCapacity must be greater than 0");

        if (minCapacity != null && maxCapacity != null && minCapacity > maxCapacity)
            throw new CustomBadRequestException("minCapacity must be less than or equal to maxCapacity");
    }

    private Set<DayOfWeek> normalizeScheduleDays(GroupScheduleType scheduleType, Set<DayOfWeek> scheduleDays) {
        if (scheduleType == null)
            throw new CustomBadRequestException("Schedule type is required");

        Set<DayOfWeek> normalizedDays = scheduleDays == null ? new HashSet<>() : new HashSet<>(scheduleDays);

        if (scheduleType == GroupScheduleType.EXACT_DAYS && normalizedDays.isEmpty())
            throw new CustomBadRequestException("Schedule days are required when schedule type is EXACT_DAYS");

        if (scheduleType != GroupScheduleType.EXACT_DAYS && !normalizedDays.isEmpty())
            throw new CustomBadRequestException("Schedule days must be empty unless schedule type is EXACT_DAYS");

        return normalizedDays;
    }

    private void validateGroupCanStart(Group group) {
        if (group.getStatus() == GroupStatus.CANCELLED)
            throw new CustomBadRequestException("Cancelled group cannot be started");

        if (group.getStatus() == GroupStatus.FINISHED)
            throw new CustomBadRequestException("Finished group cannot be started");
    }

    private void validateGroupHasNoSchedules(UUID groupId) {
        if (scheduleRepository.existsByGroupIdAndDeletedFalse(groupId))
            throw new CustomConflictException("Group already has schedules");
    }

    private Set<DayOfWeek> resolveAllowedDays(Group group) {
        if (group.getScheduleType() == GroupScheduleType.EXACT_DAYS) {
            if (group.getScheduleDays() == null || group.getScheduleDays().isEmpty())
                throw new CustomBadRequestException("Group schedule days are required for EXACT_DAYS schedule type");

            return new HashSet<>(group.getScheduleDays());
        }

        if (group.getScheduleType() == GroupScheduleType.ODD_DAYS)
            return EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY);

        if (group.getScheduleType() == GroupScheduleType.EVEN_DAYS)
            return EnumSet.of(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY, DayOfWeek.SATURDAY);

        throw new CustomBadRequestException("Group schedule type is required");
    }

    private void validateStartSchedules(List<GroupScheduleRequest> schedules, Set<DayOfWeek> allowedDays) {
        if (schedules == null || schedules.isEmpty())
            throw new CustomBadRequestException("At least one schedule is required");

        Set<DayOfWeek> requestedDays = new HashSet<>();
        for (GroupScheduleRequest schedule : schedules) {
            validateScheduleRequest(schedule);

            if (!requestedDays.add(schedule.getDayOfWeek()))
                throw new CustomBadRequestException("Only one schedule can be provided per day");
        }

        if (!requestedDays.equals(allowedDays))
            throw new CustomBadRequestException("Start schedules must exactly match the group's configured schedule days");
    }

    private void validateScheduleRequest(GroupScheduleRequest schedule) {
        if (schedule == null)
            throw new CustomBadRequestException("Schedule is required");

        if (schedule.getDayOfWeek() == null)
            throw new CustomBadRequestException("Day of week is required");

        validateScheduleTimeRange(schedule.getStartTime(), schedule.getEndTime());
    }

    private void validateScheduleTimeRange(LocalTime startTime, LocalTime endTime) {
        if (startTime == null)
            throw new CustomBadRequestException("Start time is required");

        if (endTime == null)
            throw new CustomBadRequestException("End time is required");

        if (!endTime.isAfter(startTime))
            throw new CustomBadRequestException("Schedule end time must be after start time");
    }
}
