package uz.mirmaxsudov.lmsbackend.service.impl.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.mirmaxsudov.lmsbackend.common.filter.PageableBuilder;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.StudentGroupMapper;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.StudentMapper;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomNotFoundException;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Attendance;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Course;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Group;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.LessonSession;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Room;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Schedule;
import uz.mirmaxsudov.lmsbackend.model.entity.user.StudentProfile;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.AttendanceStatus;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.GroupScheduleType;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.GroupStatus;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.LessonSessionStatus;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.StudentStatus;
import uz.mirmaxsudov.lmsbackend.model.request.user.StudentProfileRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.GroupClassmateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.NextLessonResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.StudentGroupResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.NextClassResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.ScheduleClassResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.ScheduleDayResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.StudentWeekScheduleResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.StudyGroupsOverviewResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.WeekRangeResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.WeekSummaryResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.SyllabusProgressResponse;
import uz.mirmaxsudov.lmsbackend.model.response.user.user.StudentProfileResponse;
import uz.mirmaxsudov.lmsbackend.repository.UserRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.attendance.AttendanceRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.group.GroupRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.group.GroupSpecification;
import uz.mirmaxsudov.lmsbackend.repository.lms.group.StudentGroupFilter;
import uz.mirmaxsudov.lmsbackend.repository.lms.lesson.LessonRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.lessonSession.LessonSessionRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.schedule.ScheduleRepository;
import uz.mirmaxsudov.lmsbackend.repository.user.StudentProfileRepository;
import uz.mirmaxsudov.lmsbackend.repository.user.specification.StudentProfileSpecification;
import uz.mirmaxsudov.lmsbackend.repository.user.specification.dto.StudentProfileFilter;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;
import uz.mirmaxsudov.lmsbackend.service.base.UserService;
import uz.mirmaxsudov.lmsbackend.service.base.user.StudentProfileService;
import uz.mirmaxsudov.lmsbackend.service.impl.BaseCRUDServiceImpl;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StudentProfileServiceImpl extends BaseCRUDServiceImpl<StudentProfile, StudentProfileRepository> implements StudentProfileService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final ScheduleRepository scheduleRepository;
    private final LessonSessionRepository lessonSessionRepository;
    private final LessonRepository lessonRepository;
    private final AttendanceRepository attendanceRepository;

    private static final int CLASSMATES_PREVIEW_LIMIT = 4;

    public StudentProfileServiceImpl(
            StudentProfileRepository repository,
            UserService userService,
            UserRepository userRepository,
            GroupRepository groupRepository,
            ScheduleRepository scheduleRepository,
            LessonSessionRepository lessonSessionRepository,
            LessonRepository lessonRepository,
            AttendanceRepository attendanceRepository
    ) {
        super(repository);
        this.userService = userService;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.scheduleRepository = scheduleRepository;
        this.lessonSessionRepository = lessonSessionRepository;
        this.lessonRepository = lessonRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    public ResponseEntity<ApiPaginateResponse<List<StudentProfileResponse>>> getStudentProfilePaginateResponse(int page, int size, String search, StudentStatus status) {
        int normalizedPage = Math.max(page - 1, 0);
        int normalizedSize = size <= 0 ? 10 : size;
        Pageable pageable = PageableBuilder.build(normalizedPage, normalizedSize);

        Specification<StudentProfile> filter = StudentProfileSpecification.filter(StudentProfileFilter.builder()
                .search(search)
                .status(status)
                .build());

        Page<StudentProfile> studentProfiles = repository.findAll(filter, pageable);
        List<StudentProfileResponse> results = studentProfiles.getContent().stream()
                .map(StudentMapper::toResponse)
                .toList();

        return ResponseEntity.ok(
                ApiPaginateResponse.<List<StudentProfileResponse>>builder()
                        .success(true)
                        .message("Student profiles fetched successfully")
                        .results(results)
                        .total((int) studentProfiles.getTotalElements())
                        .page(studentProfiles.getNumber() + 1)
                        .size(studentProfiles.getSize())
                        .hasNext(studentProfiles.hasNext())
                        .build()
        );
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<StudentProfileResponse>> postStudentProfile(
            StudentProfileRequest request,
            MultipartFile profileImage,
            MultipartFile profileBackgroundAttachment,
            CustomUserDetails details
    ) {
        User user = userService.createUserEntity(request, profileImage, profileBackgroundAttachment, details);
        StudentProfile profile = StudentProfile.builder()
                .user(user)
                .studentId(UUID.randomUUID())
                .status(request.getStudentStatus())
                .build();

        repository.save(profile);

        return ResponseEntity.ok(ApiResponse.<StudentProfileResponse>builder()
                .success(true)
                .message("Student profile created successfully")
                .data(StudentMapper.toResponse(profile))
                .build());
    }

    @Override
    public ResponseEntity<ApiPaginateResponse<List<StudentGroupResponse>>> getMyGroups(
            CustomUserDetails details,
            int page,
            int size,
            String search,
            GroupStatus status,
            Boolean active,
            GroupScheduleType scheduleType,
            UUID courseId
    ) {
        StudentProfile studentProfile = repository.findByUserId(details.getId())
                .orElseThrow(() -> new CustomNotFoundException("Student profile not found for current user"));

        int normalizedPage = Math.max(page - 1, 0);
        int normalizedSize = size <= 0 ? 10 : size;
        Pageable pageable = PageableBuilder.build(normalizedPage, normalizedSize);

        Specification<Group> filter = GroupSpecification.filterForStudent(StudentGroupFilter.builder()
                .studentId(studentProfile.getId())
                .search(search)
                .status(status)
                .active(active)
                .scheduleType(scheduleType)
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

        UUID currentStudentId = studentProfile.getId();
        Map<UUID, List<StudentProfile>> classmatesByGroupId = groups.getContent().stream()
                .collect(Collectors.toMap(Group::getId, group -> group.getStudents().stream()
                        .filter(student -> !student.getId().equals(currentStudentId))
                        .toList()));

        List<UUID> previewUserIds = classmatesByGroupId.values().stream()
                .flatMap(students -> students.stream().limit(CLASSMATES_PREVIEW_LIMIT))
                .map(student -> student.getUser().getId())
                .distinct()
                .toList();

        Map<UUID, User> usersById = userRepository.findAllById(previewUserIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        List<StudentGroupResponse> results = groups.getContent().stream()
                .map(group -> {
                    long totalLessons = group.getCourse() == null ? 0 : totalLessonsByCourseId.getOrDefault(group.getCourse().getId(), 0L);
                    long completedLessons = completedLessonsByGroupId.getOrDefault(group.getId(), 0L);
                    int percentage = totalLessons == 0 ? 0 : (int) Math.round(completedLessons * 100.0 / totalLessons);

                    SyllabusProgressResponse syllabusProgress = SyllabusProgressResponse.builder()
                            .completedLessons((int) completedLessons)
                            .totalLessons((int) totalLessons)
                            .percentage(percentage)
                            .build();

                    LessonSession upcomingSession = nextLessonByGroupId.get(group.getId());
                    NextLessonResponse nextLesson = upcomingSession == null ? null : NextLessonResponse.builder()
                            .lessonSessionId(upcomingSession.getId())
                            .lessonId(upcomingSession.getLesson().getId())
                            .title(upcomingSession.getLesson().getTitle())
                            .startTime(upcomingSession.getStartTime())
                            .endTime(upcomingSession.getEndTime())
                            .build();

                    List<StudentProfile> otherStudents = classmatesByGroupId.getOrDefault(group.getId(), List.of());
                    List<GroupClassmateResponse> classmates = otherStudents.stream()
                            .limit(CLASSMATES_PREVIEW_LIMIT)
                            .map(student -> {
                                User user = usersById.get(student.getUser().getId());
                                return GroupClassmateResponse.builder()
                                        .studentId(student.getId())
                                        .firstName(user == null ? null : user.getFirstName())
                                        .lastName(user == null ? null : user.getLastName())
                                        .profileImageUrl(user == null || user.getProfileImage() == null ? null : user.getProfileImage().getUrl())
                                        .build();
                            })
                            .toList();

                    return StudentGroupMapper.toResponse(
                            group,
                            schedulesByGroupId.getOrDefault(group.getId(), List.of()),
                            syllabusProgress,
                            nextLesson,
                            otherStudents.size(),
                            classmates
                    );
                })
                .toList();

        return ResponseEntity.ok(ApiPaginateResponse.<List<StudentGroupResponse>>builder()
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
    public ResponseEntity<ApiResponse<StudyGroupsOverviewResponse>> getMyStudyGroupsOverview(CustomUserDetails details) {
        StudentProfile studentProfile = repository.findByUserId(details.getId())
                .orElseThrow(() -> new CustomNotFoundException("Student profile not found for current user"));

        UUID studentId = studentProfile.getId();

        Specification<Group> filter = GroupSpecification.filterForStudent(StudentGroupFilter.builder()
                .studentId(studentId)
                .build());

        long totalGroups = groupRepository.count(filter);

        long totalClassmates = repository.countClassmates(studentId);

        LocalDateTime weekStart = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime weekEnd = weekStart.plusDays(7);
        long sessionsThisWeek = lessonSessionRepository.countByStudentIdAndStartTimeBetween(studentId, weekStart, weekEnd);

        int averageProgress = calculateAverageProgress(studentId);

        return ResponseEntity.ok(ApiResponse.<StudyGroupsOverviewResponse>builder()
                .success(true)
                .message("Study groups overview fetched successfully")
                .data(StudyGroupsOverviewResponse.builder()
                        .totalGroups(totalGroups)
                        .totalClassmates(totalClassmates)
                        .sessionsThisWeek(sessionsThisWeek)
                        .averageProgress(averageProgress)
                        .build())
                .build());
    }

    private static final ZoneOffset UZB_OFFSET = ZoneOffset.of("+05:00");

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<StudentWeekScheduleResponse>> getMyWeekSchedule(
            CustomUserDetails details,
            LocalDate from,
            LocalDate to
    ) {
        StudentProfile studentProfile = repository.findByUserId(details.getId())
                .orElseThrow(() -> new CustomNotFoundException("Student profile not found for current user"));

        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.plusDays(1).atStartOfDay();

        List<LessonSession> sessions = lessonSessionRepository.findAllByStudentAndDateRange(
                studentProfile.getId(), fromDateTime, toDateTime
        );

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        Map<LocalDate, List<LessonSession>> sessionsByDate = sessions.stream()
                .collect(Collectors.groupingBy(ls -> ls.getStartTime().toLocalDate()));

        List<ScheduleDayResponse> days = new ArrayList<>();
        LocalDate current = from;

        while (!current.isAfter(to)) {
            LocalDate date = current;
            List<LessonSession> daySessions = sessionsByDate.getOrDefault(date, List.of());

            List<ScheduleClassResponse> classes = daySessions.stream()
                    .map(this::toScheduleClassResponse)
                    .toList();

            DayOfWeek dow = date.getDayOfWeek();
            days.add(ScheduleDayResponse.builder()
                    .id(date.toString())
                    .date(date)
                    .dayNumber(date.getDayOfMonth())
                    .label(dow.getDisplayName(TextStyle.FULL, Locale.ENGLISH))
                    .shortLabel(dow.getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
                    .isToday(date.equals(today))
                    .classes(classes)
                    .build());

            current = current.plusDays(1);
        }

        int totalClasses = sessions.size();
        long totalMinutes = sessions.stream()
                .mapToLong(ls -> Duration.between(ls.getStartTime(), ls.getEndTime()).toMinutes())
                .sum();
        long totalHours = totalMinutes / 60;

        LessonSession nextSession = sessions.stream()
                .filter(ls -> ls.getStatus() == LessonSessionStatus.PLANNED && ls.getStartTime().isAfter(now))
                .findFirst()
                .orElse(null);

        NextClassResponse nextClass = null;
        if (nextSession != null) {
            LocalDate sessionDate = nextSession.getStartTime().toLocalDate();
            String dayLabel;
            if (sessionDate.equals(today))
                dayLabel = "Today";
            else if (sessionDate.equals(today.plusDays(1)))
                dayLabel = "Tomorrow";
            else {
                dayLabel = sessionDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                        + ", "
                        + sessionDate.format(DateTimeFormatter.ofPattern("MMM d"));
            }
            nextClass = NextClassResponse.builder()
                    .subject(nextSession.getGroup().getCourse().getTitle())
                    .startTime(nextSession.getStartTime().atOffset(UZB_OFFSET))
                    .dayLabel(dayLabel)
                    .build();
        }

        WeekSummaryResponse summary = WeekSummaryResponse.builder()
                .totalClasses(totalClasses)
                .totalHours(totalHours)
                .nextClass(nextClass)
                .build();

        WeekRangeResponse weekRange = WeekRangeResponse.builder()
                .from(from)
                .to(to)
                .label(buildWeekRangeLabel(from, to))
                .build();

        return ResponseEntity.ok(ApiResponse.<StudentWeekScheduleResponse>builder()
                .success(true)
                .message("Weekly schedule fetched successfully")
                .data(StudentWeekScheduleResponse.builder()
                        .weekRange(weekRange)
                        .summary(summary)
                        .days(days)
                        .build())
                .build());
    }

    private ScheduleClassResponse toScheduleClassResponse(LessonSession ls) {
        Group group = ls.getGroup();
        Room room = ls.getRoom();

        String teacherName = null;
        if (group.getTeacher() != null && group.getTeacher().getUser() != null) {
            User teacherUser = group.getTeacher().getUser();
            teacherName = teacherUser.getFirstName() + " " + teacherUser.getLastName();
        }

        String status = switch (ls.getStatus()) {
            case DONE -> "COMPLETED";
            case PLANNED -> "PLANNED";
            case CANCELLED -> "CANCELLED";
        };

        return ScheduleClassResponse.builder()
                .id(ls.getId())
                .subject(group.getCourse() != null ? group.getCourse().getTitle() : null)
                .topic(ls.getLesson().getTitle())
                .teacherName(teacherName)
                .roomName(room != null ? room.getName() : null)
                .building(room != null ? room.getBuilding() : null)
                .groupId(group.getId())
                .groupName(group.getGroupName())
                .startTime(ls.getStartTime().atOffset(UZB_OFFSET))
                .endTime(ls.getEndTime().atOffset(UZB_OFFSET))
                .status(status)
                .build();
    }

    private String buildWeekRangeLabel(LocalDate from, LocalDate to) {
        if (from.getYear() == to.getYear() && from.getMonth() == to.getMonth()) {
            return from.format(DateTimeFormatter.ofPattern("MMM d"))
                    + " – "
                    + to.getDayOfMonth()
                    + ", "
                    + from.getYear();
        } else if (from.getYear() == to.getYear()) {
            return from.format(DateTimeFormatter.ofPattern("MMM d"))
                    + " – "
                    + to.format(DateTimeFormatter.ofPattern("MMM d"))
                    + ", "
                    + from.getYear();
        } else {
            return from.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
                    + " – "
                    + to.format(DateTimeFormatter.ofPattern("MMM d, yyyy"));
        }
    }

    private int calculateAverageProgress(UUID studentId) {
        List<Attendance> attendances = attendanceRepository
                .findAllByStudent_IdAndLessonSession_StatusAndDeletedFalse(studentId, LessonSessionStatus.DONE);

        if (attendances.isEmpty())
            return 0;

        Set<AttendanceStatus> presentStatuses = Set.of(AttendanceStatus.PRESENT, AttendanceStatus.LATE);

        long presentCount = attendances.stream()
                .filter(attendance -> presentStatuses.contains(attendance.getStatus()))
                .count();

        return (int) Math.round(presentCount * 100.0 / attendances.size());
    }
}
