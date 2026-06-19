package uz.mirmaxsudov.lmsbackend.service.impl.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.mirmaxsudov.lmsbackend.common.filter.PageableBuilder;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.ScheduleMapper;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.StudentMapper;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.TeacherGroupMapper;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.TeacherMapper;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomNotFoundException;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Attendance;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Course;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Group;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.LessonSession;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Schedule;
import uz.mirmaxsudov.lmsbackend.model.entity.user.StudentProfile;
import uz.mirmaxsudov.lmsbackend.model.entity.user.TeacherProfile;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.AttendanceStatus;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.GroupStatus;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.LessonSessionStatus;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.StudentEngagementStatus;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.TeacherPosition;
import uz.mirmaxsudov.lmsbackend.model.request.user.TeacherProfileRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.NextLessonResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.SyllabusProgressResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.TeacherGroupResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.TeacherGroupStudentResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.TeacherGroupStudentsResponse;
import uz.mirmaxsudov.lmsbackend.model.response.user.user.TeacherProfileResponse;
import uz.mirmaxsudov.lmsbackend.repository.lms.attendance.AttendanceRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.group.GroupFilter;
import uz.mirmaxsudov.lmsbackend.repository.lms.group.GroupRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.group.GroupSpecification;
import uz.mirmaxsudov.lmsbackend.repository.lms.lesson.LessonRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.lessonSession.LessonSessionRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.schedule.ScheduleRepository;
import uz.mirmaxsudov.lmsbackend.repository.user.TeacherProfileRepository;
import uz.mirmaxsudov.lmsbackend.repository.user.specification.TeacherProfileSpecification;
import uz.mirmaxsudov.lmsbackend.repository.user.specification.dto.TeacherProfileFilter;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;
import uz.mirmaxsudov.lmsbackend.service.base.UserService;
import uz.mirmaxsudov.lmsbackend.service.base.user.TeacherProfileService;
import uz.mirmaxsudov.lmsbackend.service.impl.BaseCRUDServiceImpl;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TeacherProfileServiceImpl extends BaseCRUDServiceImpl<TeacherProfile, TeacherProfileRepository> implements TeacherProfileService {
    private final UserService userService;
    private final GroupRepository groupRepository;
    private final ScheduleRepository scheduleRepository;
    private final LessonSessionRepository lessonSessionRepository;
    private final LessonRepository lessonRepository;
    private final AttendanceRepository attendanceRepository;

    private static final int AT_RISK_ATTENDANCE_THRESHOLD = 60;
    private static final Set<AttendanceStatus> PRESENT_STATUSES = Set.of(AttendanceStatus.PRESENT, AttendanceStatus.LATE);

    public TeacherProfileServiceImpl(
            TeacherProfileRepository repository,
            UserService userService,
            GroupRepository groupRepository,
            ScheduleRepository scheduleRepository,
            LessonSessionRepository lessonSessionRepository,
            LessonRepository lessonRepository,
            AttendanceRepository attendanceRepository
    ) {
        super(repository);
        this.userService = userService;
        this.groupRepository = groupRepository;
        this.scheduleRepository = scheduleRepository;
        this.lessonSessionRepository = lessonSessionRepository;
        this.lessonRepository = lessonRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    public ResponseEntity<ApiPaginateResponse<List<TeacherProfileResponse>>> getTeacherProfilePaginateResponse(int page, int size, String search, TeacherPosition position) {
        int normalizedPage = Math.max(page - 1, 0);
        int normalizedSize = size <= 0 ? 10 : size;
        Pageable pageable = PageableBuilder.build(normalizedPage, normalizedSize);
        Specification<TeacherProfile> filter = TeacherProfileSpecification.filter(TeacherProfileFilter.builder()
                .search(search)
                .position(position)
                .build());

        Page<TeacherProfile> teacherProfiles = repository.findAll(filter, pageable);
        List<TeacherProfileResponse> results = teacherProfiles.getContent().stream()
                .map(TeacherMapper::toResponse)
                .toList();

        return ResponseEntity.ok(
                ApiPaginateResponse.<List<TeacherProfileResponse>>builder()
                        .success(true)
                        .message("Teacher profiles fetched successfully")
                        .results(results)
                        .total((int) teacherProfiles.getTotalElements())
                        .page(teacherProfiles.getNumber() + 1)
                        .size(teacherProfiles.getSize())
                        .hasNext(teacherProfiles.hasNext())
                        .build()
        );
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<TeacherProfileResponse>> postTeacherProfile(
            TeacherProfileRequest request,
            MultipartFile profileImage,
            MultipartFile profileBackgroundAttachment,
            CustomUserDetails details
    ) {
        User user = userService.createUserEntity(request, profileImage, profileBackgroundAttachment, details);

        TeacherProfile newTeacherProfile = TeacherProfile.builder()
                .user(user)
                .position(request.getPosition())
                .build();

        repository.save(newTeacherProfile);
        return ResponseEntity.ok(ApiResponse.<TeacherProfileResponse>builder()
                .success(true)
                .message("Teacher profile created successfully")
                .data(TeacherMapper.toResponse(newTeacherProfile))
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiPaginateResponse<List<TeacherGroupResponse>>> getMyGroups(
            CustomUserDetails details,
            int page,
            int size,
            String search,
            GroupStatus status,
            Boolean active,
            UUID courseId
    ) {
        TeacherProfile teacherProfile = repository.findByUserId(details.getId())
                .orElseThrow(() -> new CustomNotFoundException("Teacher profile not found for current user"));

        int normalizedPage = Math.max(page - 1, 0);
        int normalizedSize = size <= 0 ? 10 : size;
        Pageable pageable = PageableBuilder.build(normalizedPage, normalizedSize);

        Specification<Group> filter = GroupSpecification.filter(GroupFilter.builder()
                .teacherId(teacherProfile.getId())
                .search(search)
                .status(status)
                .active(active)
                .courseId(courseId)
                .build());

        Page<Group> groups = groupRepository.findAll(filter, pageable);

        List<UUID> groupIds = groups.getContent().stream().map(Group::getId).toList();

        Map<UUID, List<Schedule>> schedulesByGroupId = scheduleRepository.findAllByGroupIdInAndDeletedFalse(groupIds)
                .stream()
                .collect(Collectors.groupingBy(schedule -> schedule.getGroup().getId()));

        List<UUID> courseIds = groups.getContent().stream()
                .map(Group::getCourse)
                .filter(Objects::nonNull)
                .map(Course::getId)
                .distinct()
                .toList();

        Map<UUID, Long> totalLessonsByCourseId = lessonRepository.countActiveByCourseIds(courseIds).stream()
                .collect(Collectors.toMap(LessonRepository.CourseLessonCount::getCourseId, LessonRepository.CourseLessonCount::getLessonCount));

        Map<UUID, Long> completedLessonsByGroupId = lessonSessionRepository.countByGroupIdsAndStatus(groupIds, LessonSessionStatus.DONE).stream()
                .collect(Collectors.toMap(LessonSessionRepository.GroupSessionCount::getGroupId, LessonSessionRepository.GroupSessionCount::getSessionCount));

        Map<UUID, LessonSession> nextLessonByGroupId = lessonSessionRepository
                .findUpcomingByGroupIdsAndStatus(groupIds, LessonSessionStatus.PLANNED, LocalDateTime.now())
                .stream()
                .collect(Collectors.toMap(
                        session -> session.getGroup().getId(),
                        Function.identity(),
                        (first, second) -> first
                ));

        List<TeacherGroupResponse> results = groups.getContent().stream()
                .map(group -> {
                    SyllabusProgressResponse syllabusProgress = buildSyllabusProgress(group, totalLessonsByCourseId, completedLessonsByGroupId);

                    LessonSession upcomingSession = nextLessonByGroupId.get(group.getId());
                    NextLessonResponse nextLesson = upcomingSession == null ? null : NextLessonResponse.builder()
                            .lessonSessionId(upcomingSession.getId())
                            .lessonId(upcomingSession.getLesson().getId())
                            .title(upcomingSession.getLesson().getTitle())
                            .startTime(upcomingSession.getStartTime())
                            .endTime(upcomingSession.getEndTime())
                            .build();

                    return TeacherGroupMapper.toResponse(
                            group,
                            schedulesByGroupId.getOrDefault(group.getId(), List.of()),
                            syllabusProgress,
                            nextLesson
                    );
                })
                .toList();

        return ResponseEntity.ok(ApiPaginateResponse.<List<TeacherGroupResponse>>builder()
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
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<TeacherGroupStudentsResponse>> getMyGroupStudents(
            CustomUserDetails details,
            UUID groupId
    ) {
        TeacherProfile teacherProfile = repository.findByUserId(details.getId())
                .orElseThrow(() -> new CustomNotFoundException("Teacher profile not found for current user"));

        Group group = groupRepository.findByIdAndDeletedFalse(groupId)
                .orElseThrow(() -> new CustomNotFoundException("Group not found"));

        if (group.getTeacher() == null || !group.getTeacher().getId().equals(teacherProfile.getId())) {
            throw new CustomNotFoundException("Group not found for current teacher");
        }

        List<Schedule> schedules = scheduleRepository.findAllByGroupIdInAndDeletedFalse(List.of(groupId));

        SyllabusProgressResponse progress = buildSyllabusProgress(
                group,
                lessonRepository.countActiveByCourseIds(group.getCourse() == null ? List.of() : List.of(group.getCourse().getId())).stream()
                        .collect(Collectors.toMap(LessonRepository.CourseLessonCount::getCourseId, LessonRepository.CourseLessonCount::getLessonCount)),
                lessonSessionRepository.countByGroupIdsAndStatus(List.of(groupId), LessonSessionStatus.DONE).stream()
                        .collect(Collectors.toMap(LessonSessionRepository.GroupSessionCount::getGroupId, LessonSessionRepository.GroupSessionCount::getSessionCount))
        );

        Map<UUID, List<Attendance>> attendancesByStudentId = attendanceRepository
                .findAllByGroupIdAndLessonSessionStatus(groupId, LessonSessionStatus.DONE)
                .stream()
                .collect(Collectors.groupingBy(attendance -> attendance.getStudent().getId()));

        List<TeacherGroupStudentResponse> students = group.getStudents().stream()
                .sorted(Comparator.comparing(student -> student.getUser() == null ? "" : student.getUser().getFirstName(),
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .map(student -> {
                    int avgAttendance = calculateAttendancePercentage(attendancesByStudentId.getOrDefault(student.getId(), List.of()));
                    StudentEngagementStatus engagementStatus = avgAttendance < AT_RISK_ATTENDANCE_THRESHOLD
                            ? StudentEngagementStatus.AT_RISK
                            : StudentEngagementStatus.ACTIVE;

                    return TeacherGroupStudentResponse.builder()
                            .student(StudentMapper.toResponse(student))
                            .avgAttendance(avgAttendance)
                            .engagementStatus(engagementStatus)
                            .build();
                })
                .toList();

        TeacherGroupStudentsResponse response = TeacherGroupStudentsResponse.builder()
                .groupId(group.getId())
                .groupName(group.getGroupName())
                .totalStudents(students.size())
                .progress(progress)
                .scheduleDays(group.getScheduleDays())
                .schedules(schedules.stream().map(ScheduleMapper::toResponse).toList())
                .students(students)
                .build();

        return ResponseEntity.ok(ApiResponse.<TeacherGroupStudentsResponse>builder()
                .success(true)
                .message("Group students fetched successfully")
                .data(response)
                .build());
    }

    private SyllabusProgressResponse buildSyllabusProgress(
            Group group,
            Map<UUID, Long> totalLessonsByCourseId,
            Map<UUID, Long> completedLessonsByGroupId
    ) {
        long totalLessons = group.getCourse() == null ? 0 : totalLessonsByCourseId.getOrDefault(group.getCourse().getId(), 0L);
        long completedLessons = completedLessonsByGroupId.getOrDefault(group.getId(), 0L);
        int percentage = totalLessons == 0 ? 0 : (int) Math.round(completedLessons * 100.0 / totalLessons);

        return SyllabusProgressResponse.builder()
                .completedLessons((int) completedLessons)
                .totalLessons((int) totalLessons)
                .percentage(percentage)
                .build();
    }

    private int calculateAttendancePercentage(List<Attendance> attendances) {
        if (attendances.isEmpty()) {
            return 0;
        }

        long presentCount = attendances.stream()
                .filter(attendance -> PRESENT_STATUSES.contains(attendance.getStatus()))
                .count();

        return (int) Math.round(presentCount * 100.0 / attendances.size());
    }
}
