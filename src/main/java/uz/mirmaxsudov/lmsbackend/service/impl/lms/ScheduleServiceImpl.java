package uz.mirmaxsudov.lmsbackend.service.impl.lms;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.mirmaxsudov.lmsbackend.common.filter.PageableBuilder;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.ScheduleMapper;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomBadRequestException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomConflictException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomNotFoundException;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Group;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Schedule;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.GroupScheduleType;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.GroupStatus;
import uz.mirmaxsudov.lmsbackend.model.request.lms.ScheduleCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.ScheduleUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.ScheduleResponse;
import uz.mirmaxsudov.lmsbackend.repository.lms.group.GroupRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.schedule.ScheduleFilter;
import uz.mirmaxsudov.lmsbackend.repository.lms.schedule.ScheduleRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.schedule.ScheduleSpecification;
import uz.mirmaxsudov.lmsbackend.service.base.lms.ScheduleService;
import uz.mirmaxsudov.lmsbackend.service.impl.BaseCRUDServiceImpl;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ScheduleServiceImpl extends BaseCRUDServiceImpl<Schedule, ScheduleRepository> implements ScheduleService {
    private final GroupRepository groupRepository;

    public ScheduleServiceImpl(ScheduleRepository repository, GroupRepository groupRepository) {
        super(repository);
        this.groupRepository = groupRepository;
    }

    @Override
    public ResponseEntity<ApiPaginateResponse<List<ScheduleResponse>>> getAll(
            int page,
            int size,
            UUID groupId,
            DayOfWeek dayOfWeek
    ) {
        int normalizedPage = Math.max(page - 1, 0);
        int normalizedSize = size <= 0 ? 10 : size;

        Pageable pageable = PageableBuilder.build(normalizedPage, normalizedSize);
        Specification<Schedule> filter = ScheduleSpecification.filter(ScheduleFilter.builder()
                .groupId(groupId)
                .dayOfWeek(dayOfWeek)
                .build());

        Page<Schedule> schedules = repository.findAll(filter, pageable);
        List<ScheduleResponse> results = schedules.getContent().stream()
                .map(ScheduleMapper::toResponse)
                .toList();

        return ResponseEntity.ok(ApiPaginateResponse.<List<ScheduleResponse>>builder()
                .success(true)
                .message("Schedules fetched successfully")
                .results(results)
                .total((int) schedules.getTotalElements())
                .page(schedules.getNumber() + 1)
                .size(schedules.getSize())
                .hasNext(schedules.hasNext())
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<ScheduleResponse>> getByIdResponse(UUID id) {
        Schedule schedule = findActiveSchedule(id);

        return ResponseEntity.ok(ApiResponse.<ScheduleResponse>builder()
                .success(true)
                .message("Schedule fetched successfully")
                .data(ScheduleMapper.toResponse(schedule))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<ScheduleResponse>> createSchedule(ScheduleCreateRequest request) {
        Group group = findActiveGroup(request.getGroupId());
        validateGroupCanReceiveSchedule(group);
        validateScheduleDayAllowed(group, request.getDayOfWeek());
        validateTimeRange(request.getStartTime(), request.getEndTime());
        validateDuplicate(group.getId(), request.getDayOfWeek(), request.getStartTime(), request.getEndTime(), null);

        Schedule schedule = Schedule.builder()
                .group(group)
                .dayOfWeek(request.getDayOfWeek())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();

        Schedule savedSchedule = repository.save(schedule);

        return ResponseEntity.ok(ApiResponse.<ScheduleResponse>builder()
                .success(true)
                .message("Schedule created successfully")
                .data(ScheduleMapper.toResponse(savedSchedule))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<ScheduleResponse>> updateSchedule(UUID id, ScheduleUpdateRequest request) {
        Schedule existingSchedule = findActiveSchedule(id);
        Group group = findActiveGroup(request.getGroupId());

        validateGroupCanReceiveSchedule(group);
        validateScheduleDayAllowed(group, request.getDayOfWeek());
        validateTimeRange(request.getStartTime(), request.getEndTime());
        validateDuplicate(group.getId(), request.getDayOfWeek(), request.getStartTime(), request.getEndTime(), id);

        existingSchedule.setGroup(group);
        existingSchedule.setDayOfWeek(request.getDayOfWeek());
        existingSchedule.setStartTime(request.getStartTime());
        existingSchedule.setEndTime(request.getEndTime());

        Schedule updatedSchedule = repository.save(existingSchedule);

        return ResponseEntity.ok(ApiResponse.<ScheduleResponse>builder()
                .success(true)
                .message("Schedule updated successfully")
                .data(ScheduleMapper.toResponse(updatedSchedule))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(UUID id) {
        Schedule existingSchedule = findActiveSchedule(id);

        existingSchedule.setDeleted(true);
        existingSchedule.setDeletedAt(LocalDateTime.now());
        repository.save(existingSchedule);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Schedule deleted successfully")
                .build());
    }

    private Schedule findActiveSchedule(UUID id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new CustomNotFoundException("Schedule not found with id: " + id));
    }

    private Group findActiveGroup(UUID groupId) {
        return groupRepository.findByIdAndDeletedFalse(groupId)
                .orElseThrow(() -> new CustomNotFoundException("Group not found with id: " + groupId));
    }

    private void validateGroupCanReceiveSchedule(Group group) {
        if (group.getStatus() != GroupStatus.ACTIVE)
            throw new CustomBadRequestException("Schedule can be created only for active groups");
    }

    private void validateScheduleDayAllowed(Group group, DayOfWeek dayOfWeek) {
        if (dayOfWeek == null)
            throw new CustomBadRequestException("Day of week is required");

        Set<DayOfWeek> allowedDays = resolveAllowedDays(group);
        if (!allowedDays.contains(dayOfWeek))
            throw new CustomBadRequestException("Schedule day is not allowed by the group's schedule configuration");
    }

    private Set<DayOfWeek> resolveAllowedDays(Group group) {
        if (group.getScheduleType() == GroupScheduleType.EXACT_DAYS) {
            if (group.getScheduleDays() == null || group.getScheduleDays().isEmpty())
                throw new CustomBadRequestException("Group schedule days are required for EXACT_DAYS schedule type");

            return group.getScheduleDays();
        }

        if (group.getScheduleType() == GroupScheduleType.ODD_DAYS)
            return EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY);

        if (group.getScheduleType() == GroupScheduleType.EVEN_DAYS)
            return EnumSet.of(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY, DayOfWeek.SATURDAY);

        throw new CustomBadRequestException("Group schedule type is required");
    }

    private void validateTimeRange(LocalTime startTime, LocalTime endTime) {
        if (startTime == null)
            throw new CustomBadRequestException("Start time is required");

        if (endTime == null)
            throw new CustomBadRequestException("End time is required");

        if (!endTime.isAfter(startTime))
            throw new CustomBadRequestException("Schedule end time must be after start time");
    }

    private void validateDuplicate(
            UUID groupId,
            DayOfWeek dayOfWeek,
            LocalTime startTime,
            LocalTime endTime,
            UUID excludedScheduleId
    ) {
        boolean exists = excludedScheduleId == null
                ? repository.existsByGroupIdAndDayOfWeekAndStartTimeAndEndTimeAndDeletedFalse(
                groupId,
                dayOfWeek,
                startTime,
                endTime
        )
                : repository.existsByGroupIdAndDayOfWeekAndStartTimeAndEndTimeAndIdNotAndDeletedFalse(
                groupId,
                dayOfWeek,
                startTime,
                endTime,
                excludedScheduleId
        );

        if (exists)
            throw new CustomConflictException("Schedule already exists for this group, day, and time range");
    }
}
