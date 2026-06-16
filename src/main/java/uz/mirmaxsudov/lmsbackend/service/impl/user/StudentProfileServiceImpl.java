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
import uz.mirmaxsudov.lmsbackend.model.response.lms.StudyGroupsOverviewResponse;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
