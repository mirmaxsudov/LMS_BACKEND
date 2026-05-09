package uz.mirmaxsudov.lmsbackend.service.impl.lms.online;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.mirmaxsudov.lmsbackend.common.filter.PageableBuilder;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.OnlineCourseMapper;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomBadRequestException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomConflictException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomNotFoundException;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.entity.base.BaseEntity;
import uz.mirmaxsudov.lmsbackend.model.entity.content.Attachment;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.online.OnlineCourse;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.online.OnlineCourseEnrollment;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.online.OnlineCourseLesson;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.online.OnlineCourseLessonMaterial;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.online.OnlineCourseLessonProgress;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.online.OnlineCourseModule;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.online.OnlineCourseModuleProgress;
import uz.mirmaxsudov.lmsbackend.model.entity.user.StudentProfile;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.CourseLevel;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.online.OnlineCourseContentStatus;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.online.OnlineCourseEnrollmentStatus;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.online.OnlineCourseProgressStatus;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.online.OnlineCourseStatus;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.online.OnlineCourseUnlockStrategy;
import uz.mirmaxsudov.lmsbackend.model.request.lms.online.OnlineCourseCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.online.OnlineCourseEnrollmentCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.online.OnlineCourseLessonCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.online.OnlineCourseLessonMaterialRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.online.OnlineCourseLessonProgressRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.online.OnlineCourseLessonUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.online.OnlineCourseModuleCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.online.OnlineCourseModuleUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.online.OnlineCourseUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.online.OnlineCourseEnrollmentResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.online.OnlineCourseLessonMaterialResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.online.OnlineCourseLessonResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.online.OnlineCourseModuleResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.online.OnlineCourseResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.online.OnlineCourseSummaryResponse;
import uz.mirmaxsudov.lmsbackend.repository.content.AttachmentRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.online.OnlineCourseEnrollmentFilter;
import uz.mirmaxsudov.lmsbackend.repository.lms.online.OnlineCourseEnrollmentRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.online.OnlineCourseEnrollmentSpecification;
import uz.mirmaxsudov.lmsbackend.repository.lms.online.OnlineCourseFilter;
import uz.mirmaxsudov.lmsbackend.repository.lms.online.OnlineCourseLessonMaterialRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.online.OnlineCourseLessonProgressRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.online.OnlineCourseLessonRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.online.OnlineCourseModuleProgressRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.online.OnlineCourseModuleRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.online.OnlineCourseRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.online.OnlineCourseSpecification;
import uz.mirmaxsudov.lmsbackend.repository.user.ParentProfileRepository;
import uz.mirmaxsudov.lmsbackend.repository.user.StudentProfileRepository;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;
import uz.mirmaxsudov.lmsbackend.service.base.lms.online.OnlineCourseService;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Service
public class OnlineCourseServiceImpl implements OnlineCourseService {
    private static final String ROLE_SUPER_ADMIN = "ROLE_SUPER_ADMIN";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_MAINTAINER = "ROLE_MAINTAINER";
    private static final String ROLE_TEACHER = "ROLE_TEACHER";
    private static final String ROLE_SUPPORT_TEACHER = "ROLE_SUPPORT_TEACHER";
    private static final String ROLE_STUDENT = "ROLE_STUDENT";
    private static final String ROLE_PARENT = "ROLE_PARENT";
    private static final String ROLE_GUARDIAN = "ROLE_GUARDIAN";

    private final OnlineCourseRepository courseRepository;
    private final OnlineCourseModuleRepository moduleRepository;
    private final OnlineCourseLessonRepository lessonRepository;
    private final OnlineCourseLessonMaterialRepository materialRepository;
    private final OnlineCourseEnrollmentRepository enrollmentRepository;
    private final OnlineCourseLessonProgressRepository lessonProgressRepository;
    private final OnlineCourseModuleProgressRepository moduleProgressRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final ParentProfileRepository parentProfileRepository;
    private final AttachmentRepository attachmentRepository;

    public OnlineCourseServiceImpl(
            OnlineCourseRepository courseRepository,
            OnlineCourseModuleRepository moduleRepository,
            OnlineCourseLessonRepository lessonRepository,
            OnlineCourseLessonMaterialRepository materialRepository,
            OnlineCourseEnrollmentRepository enrollmentRepository,
            OnlineCourseLessonProgressRepository lessonProgressRepository,
            OnlineCourseModuleProgressRepository moduleProgressRepository,
            StudentProfileRepository studentProfileRepository,
            ParentProfileRepository parentProfileRepository,
            AttachmentRepository attachmentRepository
    ) {
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository;
        this.lessonRepository = lessonRepository;
        this.materialRepository = materialRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.lessonProgressRepository = lessonProgressRepository;
        this.moduleProgressRepository = moduleProgressRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.parentProfileRepository = parentProfileRepository;
        this.attachmentRepository = attachmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiPaginateResponse<List<OnlineCourseSummaryResponse>>> getAllCourses(
            int page,
            int size,
            String search,
            CourseLevel level,
            OnlineCourseStatus status,
            UUID createdById,
            Integer minDurationInMinutes,
            Integer maxDurationInMinutes,
            CustomUserDetails details
    ) {
        validateDurationRange(minDurationInMinutes, maxDurationInMinutes);

        boolean canSeeAll = canManageAll(details) || canAuthor(details);
        OnlineCourseStatus effectiveStatus = canSeeAll ? status : OnlineCourseStatus.PUBLISHED;
        UUID effectiveCreatedById = canSeeAll ? createdById : null;

        Pageable pageable = PageableBuilder.build(Math.max(page - 1, 0), size <= 0 ? 10 : size);
        Specification<OnlineCourse> specification = OnlineCourseSpecification.filter(OnlineCourseFilter.builder()
                .search(search)
                .level(level)
                .status(effectiveStatus)
                .createdById(effectiveCreatedById)
                .minDurationInMinutes(minDurationInMinutes)
                .maxDurationInMinutes(maxDurationInMinutes)
                .build());

        Page<OnlineCourse> courses = courseRepository.findAll(specification, pageable);
        List<OnlineCourseSummaryResponse> results = courses.getContent().stream()
                .map(OnlineCourseMapper::toSummary)
                .toList();

        return ResponseEntity.ok(ApiPaginateResponse.<List<OnlineCourseSummaryResponse>>builder()
                .success(true)
                .message("Online courses fetched successfully")
                .results(results)
                .total((int) courses.getTotalElements())
                .page(courses.getNumber() + 1)
                .size(courses.getSize())
                .hasNext(courses.hasNext())
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<OnlineCourseResponse>> getCourseById(UUID courseId, CustomUserDetails details) {
        OnlineCourse course = findActiveCourse(courseId);
        assertCanViewCourse(course, details);
        boolean includeDraftContent = canManageCourse(course, details);

        return ResponseEntity.ok(ApiResponse.<OnlineCourseResponse>builder()
                .success(true)
                .message("Online course fetched successfully")
                .data(OnlineCourseMapper.toResponse(course, includeDraftContent))
                .build());
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<OnlineCourseResponse>> createCourse(OnlineCourseCreateRequest request, CustomUserDetails details) {
        assertCanAuthor(details);

        String slug = normalizeSlug(request.getSlug(), request.getTitle());
        ensureSlugAvailable(slug, null);

        OnlineCourse course = OnlineCourse.builder()
                .title(request.getTitle().trim())
                .slug(slug)
                .shortDescription(normalizeNullable(request.getShortDescription()))
                .description(normalizeNullable(request.getDescription()))
                .level(request.getLevel())
                .status(request.getStatus() == null ? OnlineCourseStatus.DRAFT : request.getStatus())
                .unlockStrategy(request.getUnlockStrategy() == null ? OnlineCourseUnlockStrategy.LESSON_BY_LESSON : request.getUnlockStrategy())
                .estimatedDurationInMinutes(request.getEstimatedDurationInMinutes())
                .thumbnail(findOptionalAttachment(request.getThumbnailId(), "Thumbnail attachment not found"))
                .createdBy(details.user())
                .build();

        OnlineCourse savedCourse = courseRepository.save(course);

        return ResponseEntity.ok(ApiResponse.<OnlineCourseResponse>builder()
                .success(true)
                .message("Online course created successfully")
                .data(OnlineCourseMapper.toResponse(savedCourse, true))
                .build());
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<OnlineCourseResponse>> updateCourse(UUID courseId, OnlineCourseUpdateRequest request, CustomUserDetails details) {
        OnlineCourse course = findActiveCourse(courseId);
        assertCanManageCourse(course, details);

        String slug = normalizeSlug(request.getSlug(), request.getTitle());
        ensureSlugAvailable(slug, courseId);

        course.setTitle(request.getTitle().trim());
        course.setSlug(slug);
        course.setShortDescription(normalizeNullable(request.getShortDescription()));
        course.setDescription(normalizeNullable(request.getDescription()));
        course.setLevel(request.getLevel());
        course.setStatus(request.getStatus());
        course.setUnlockStrategy(request.getUnlockStrategy());
        course.setEstimatedDurationInMinutes(request.getEstimatedDurationInMinutes());
        course.setThumbnail(findOptionalAttachment(request.getThumbnailId(), "Thumbnail attachment not found"));

        return ResponseEntity.ok(ApiResponse.<OnlineCourseResponse>builder()
                .success(true)
                .message("Online course updated successfully")
                .data(OnlineCourseMapper.toResponse(courseRepository.save(course), true))
                .build());
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<Void>> deleteCourse(UUID courseId, CustomUserDetails details) {
        OnlineCourse course = findActiveCourse(courseId);
        assertCanManageCourse(course, details);
        softDelete(course);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Online course deleted successfully")
                .build());
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<OnlineCourseModuleResponse>> createModule(UUID courseId, OnlineCourseModuleCreateRequest request, CustomUserDetails details) {
        OnlineCourse course = findActiveCourse(courseId);
        assertCanManageCourse(course, details);
        ensureModuleOrderAvailable(course.getId(), request.getOrderIndex(), null);

        OnlineCourseModule module = OnlineCourseModule.builder()
                .course(course)
                .title(request.getTitle().trim())
                .description(normalizeNullable(request.getDescription()))
                .orderIndex(request.getOrderIndex())
                .status(request.getStatus() == null ? OnlineCourseContentStatus.DRAFT : request.getStatus())
                .availableFrom(request.getAvailableFrom())
                .build();

        OnlineCourseModule savedModule = moduleRepository.save(module);

        return ResponseEntity.ok(ApiResponse.<OnlineCourseModuleResponse>builder()
                .success(true)
                .message("Online course module created successfully")
                .data(OnlineCourseMapper.toModuleResponse(savedModule, true))
                .build());
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<OnlineCourseModuleResponse>> updateModule(UUID moduleId, OnlineCourseModuleUpdateRequest request, CustomUserDetails details) {
        OnlineCourseModule module = findActiveModule(moduleId);
        assertCanManageCourse(module.getCourse(), details);
        ensureModuleOrderAvailable(module.getCourse().getId(), request.getOrderIndex(), moduleId);

        module.setTitle(request.getTitle().trim());
        module.setDescription(normalizeNullable(request.getDescription()));
        module.setOrderIndex(request.getOrderIndex());
        module.setStatus(request.getStatus());
        module.setAvailableFrom(request.getAvailableFrom());

        return ResponseEntity.ok(ApiResponse.<OnlineCourseModuleResponse>builder()
                .success(true)
                .message("Online course module updated successfully")
                .data(OnlineCourseMapper.toModuleResponse(moduleRepository.save(module), true))
                .build());
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<Void>> deleteModule(UUID moduleId, CustomUserDetails details) {
        OnlineCourseModule module = findActiveModule(moduleId);
        assertCanManageCourse(module.getCourse(), details);
        softDelete(module);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Online course module deleted successfully")
                .build());
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<OnlineCourseLessonResponse>> createLesson(UUID moduleId, OnlineCourseLessonCreateRequest request, CustomUserDetails details) {
        OnlineCourseModule module = findActiveModule(moduleId);
        assertCanManageCourse(module.getCourse(), details);
        ensureLessonOrderAvailable(module.getId(), request.getOrderIndex(), null);

        OnlineCourseLesson lesson = OnlineCourseLesson.builder()
                .module(module)
                .title(request.getTitle().trim())
                .description(normalizeNullable(request.getDescription()))
                .content(normalizeNullable(request.getContent()))
                .orderIndex(request.getOrderIndex())
                .durationInMinutes(request.getDurationInMinutes())
                .freePreview(request.isFreePreview())
                .status(request.getStatus() == null ? OnlineCourseContentStatus.DRAFT : request.getStatus())
                .availableFrom(request.getAvailableFrom())
                .videoAttachment(findOptionalAttachment(request.getVideoAttachmentId(), "Video attachment not found"))
                .build();

        OnlineCourseLesson savedLesson = lessonRepository.save(lesson);

        return ResponseEntity.ok(ApiResponse.<OnlineCourseLessonResponse>builder()
                .success(true)
                .message("Online course lesson created successfully")
                .data(OnlineCourseMapper.toLessonResponse(savedLesson, true))
                .build());
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<OnlineCourseLessonResponse>> updateLesson(UUID lessonId, OnlineCourseLessonUpdateRequest request, CustomUserDetails details) {
        OnlineCourseLesson lesson = findActiveLesson(lessonId);
        assertCanManageCourse(lesson.getModule().getCourse(), details);
        ensureLessonOrderAvailable(lesson.getModule().getId(), request.getOrderIndex(), lessonId);

        lesson.setTitle(request.getTitle().trim());
        lesson.setDescription(normalizeNullable(request.getDescription()));
        lesson.setContent(normalizeNullable(request.getContent()));
        lesson.setOrderIndex(request.getOrderIndex());
        lesson.setDurationInMinutes(request.getDurationInMinutes());
        lesson.setFreePreview(request.isFreePreview());
        lesson.setStatus(request.getStatus());
        lesson.setAvailableFrom(request.getAvailableFrom());
        lesson.setVideoAttachment(findOptionalAttachment(request.getVideoAttachmentId(), "Video attachment not found"));

        return ResponseEntity.ok(ApiResponse.<OnlineCourseLessonResponse>builder()
                .success(true)
                .message("Online course lesson updated successfully")
                .data(OnlineCourseMapper.toLessonResponse(lessonRepository.save(lesson), true))
                .build());
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<Void>> deleteLesson(UUID lessonId, CustomUserDetails details) {
        OnlineCourseLesson lesson = findActiveLesson(lessonId);
        assertCanManageCourse(lesson.getModule().getCourse(), details);
        softDelete(lesson);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Online course lesson deleted successfully")
                .build());
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<OnlineCourseLessonMaterialResponse>> addLessonMaterial(UUID lessonId, OnlineCourseLessonMaterialRequest request, CustomUserDetails details) {
        OnlineCourseLesson lesson = findActiveLesson(lessonId);
        assertCanManageCourse(lesson.getModule().getCourse(), details);

        if (materialRepository.existsByLessonIdAndAttachmentIdAndDeletedFalse(lessonId, request.getAttachmentId()))
            throw new CustomConflictException("Attachment is already added to this lesson");

        OnlineCourseLessonMaterial material = OnlineCourseLessonMaterial.builder()
                .lesson(lesson)
                .attachment(findRequiredAttachment(request.getAttachmentId(), "Material attachment not found"))
                .title(normalizeNullable(request.getTitle()))
                .orderIndex(request.getOrderIndex())
                .build();

        OnlineCourseLessonMaterial savedMaterial = materialRepository.save(material);

        return ResponseEntity.ok(ApiResponse.<OnlineCourseLessonMaterialResponse>builder()
                .success(true)
                .message("Online course lesson material added successfully")
                .data(OnlineCourseMapper.toMaterialResponse(savedMaterial))
                .build());
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<OnlineCourseLessonMaterialResponse>> updateLessonMaterial(UUID materialId, OnlineCourseLessonMaterialRequest request, CustomUserDetails details) {
        OnlineCourseLessonMaterial material = findActiveMaterial(materialId);
        assertCanManageCourse(material.getLesson().getModule().getCourse(), details);

        if (materialRepository.existsByLessonIdAndAttachmentIdAndIdNotAndDeletedFalse(material.getLesson().getId(), request.getAttachmentId(), materialId))
            throw new CustomConflictException("Attachment is already added to this lesson");

        material.setAttachment(findRequiredAttachment(request.getAttachmentId(), "Material attachment not found"));
        material.setTitle(normalizeNullable(request.getTitle()));
        material.setOrderIndex(request.getOrderIndex());

        return ResponseEntity.ok(ApiResponse.<OnlineCourseLessonMaterialResponse>builder()
                .success(true)
                .message("Online course lesson material updated successfully")
                .data(OnlineCourseMapper.toMaterialResponse(materialRepository.save(material)))
                .build());
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<Void>> deleteLessonMaterial(UUID materialId, CustomUserDetails details) {
        OnlineCourseLessonMaterial material = findActiveMaterial(materialId);
        assertCanManageCourse(material.getLesson().getModule().getCourse(), details);
        softDelete(material);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Online course lesson material deleted successfully")
                .build());
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<OnlineCourseEnrollmentResponse>> enrollMe(UUID courseId, CustomUserDetails details) {
        assertHasRole(details, ROLE_STUDENT);
        StudentProfile student = findStudentByUser(details.getId());
        OnlineCourseEnrollment enrollment = openEnrollment(courseId, student, details.user(), OnlineCourseEnrollmentStatus.ACTIVE);

        return ResponseEntity.ok(ApiResponse.<OnlineCourseEnrollmentResponse>builder()
                .success(true)
                .message("Online course enrollment created successfully")
                .data(OnlineCourseMapper.toEnrollmentResponse(enrollment))
                .build());
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<OnlineCourseEnrollmentResponse>> createEnrollment(UUID courseId, OnlineCourseEnrollmentCreateRequest request, CustomUserDetails details) {
        assertCanManageAll(details);
        StudentProfile student = findActiveStudent(request.getStudentProfileId());
        OnlineCourseEnrollment enrollment = openEnrollment(
                courseId,
                student,
                details.user(),
                request.getStatus() == null ? OnlineCourseEnrollmentStatus.ACTIVE : request.getStatus()
        );

        return ResponseEntity.ok(ApiResponse.<OnlineCourseEnrollmentResponse>builder()
                .success(true)
                .message("Online course enrollment created successfully")
                .data(OnlineCourseMapper.toEnrollmentResponse(enrollment))
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiPaginateResponse<List<OnlineCourseEnrollmentResponse>>> getMyEnrollments(
            int page,
            int size,
            OnlineCourseEnrollmentStatus status,
            CustomUserDetails details
    ) {
        assertHasRole(details, ROLE_STUDENT);
        StudentProfile student = findStudentByUser(details.getId());
        return getEnrollmentPage(page, size, null, student.getId(), status, "Online course enrollments fetched successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiPaginateResponse<List<OnlineCourseEnrollmentResponse>>> getEnrollments(
            int page,
            int size,
            UUID courseId,
            UUID studentProfileId,
            OnlineCourseEnrollmentStatus status,
            CustomUserDetails details
    ) {
        assertCanManageAll(details);
        return getEnrollmentPage(page, size, courseId, studentProfileId, status, "Online course enrollments fetched successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiPaginateResponse<List<OnlineCourseEnrollmentResponse>>> getStudentEnrollments(
            UUID studentProfileId,
            int page,
            int size,
            OnlineCourseEnrollmentStatus status,
            CustomUserDetails details
    ) {
        assertCanViewStudent(studentProfileId, details);
        return getEnrollmentPage(page, size, null, studentProfileId, status, "Student online course enrollments fetched successfully");
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<OnlineCourseEnrollmentResponse>> updateLessonProgress(
            UUID lessonId,
            OnlineCourseLessonProgressRequest request,
            CustomUserDetails details
    ) {
        assertHasRole(details, ROLE_STUDENT);
        validateProgressStatus(request.getStatus());

        StudentProfile student = findStudentByUser(details.getId());
        OnlineCourseLesson lesson = findActiveLesson(lessonId);
        OnlineCourse course = lesson.getModule().getCourse();
        OnlineCourseEnrollment enrollment = enrollmentRepository.findByCourseIdAndStudentIdAndDeletedFalse(course.getId(), student.getId())
                .orElseThrow(() -> new CustomNotFoundException("Active enrollment not found for this course"));

        if (!OnlineCourseEnrollmentStatus.ACTIVE.equals(enrollment.getStatus()))
            throw new CustomBadRequestException("Only active enrollments can update progress");

        OnlineCourseLessonProgress progress = lessonProgressRepository.findByEnrollmentIdAndLessonIdAndDeletedFalse(enrollment.getId(), lessonId)
                .orElseThrow(() -> new CustomBadRequestException("Lesson is locked or not available for this enrollment"));

        if (OnlineCourseProgressStatus.LOCKED.equals(progress.getStatus()))
            throw new CustomBadRequestException("Lesson is locked");

        applyLessonProgress(progress, request);
        enrollment.setCurrentModule(lesson.getModule());
        enrollment.setCurrentLesson(lesson);
        refreshModuleProgress(enrollment, lesson.getModule());
        unlockNextContent(enrollment);
        completeEnrollmentIfReady(enrollment);

        return ResponseEntity.ok(ApiResponse.<OnlineCourseEnrollmentResponse>builder()
                .success(true)
                .message("Online course lesson progress updated successfully")
                .data(OnlineCourseMapper.toEnrollmentResponse(enrollmentRepository.save(enrollment)))
                .build());
    }

    private ResponseEntity<ApiPaginateResponse<List<OnlineCourseEnrollmentResponse>>> getEnrollmentPage(
            int page,
            int size,
            UUID courseId,
            UUID studentProfileId,
            OnlineCourseEnrollmentStatus status,
            String message
    ) {
        Pageable pageable = PageableBuilder.build(Math.max(page - 1, 0), size <= 0 ? 10 : size);
        Specification<OnlineCourseEnrollment> specification = OnlineCourseEnrollmentSpecification.filter(OnlineCourseEnrollmentFilter.builder()
                .courseId(courseId)
                .studentProfileId(studentProfileId)
                .status(status)
                .build());

        Page<OnlineCourseEnrollment> enrollments = enrollmentRepository.findAll(specification, pageable);
        List<OnlineCourseEnrollmentResponse> results = enrollments.getContent().stream()
                .map(OnlineCourseMapper::toEnrollmentResponse)
                .toList();

        return ResponseEntity.ok(ApiPaginateResponse.<List<OnlineCourseEnrollmentResponse>>builder()
                .success(true)
                .message(message)
                .results(results)
                .total((int) enrollments.getTotalElements())
                .page(enrollments.getNumber() + 1)
                .size(enrollments.getSize())
                .hasNext(enrollments.hasNext())
                .build());
    }

    private OnlineCourseEnrollment openEnrollment(
            UUID courseId,
            StudentProfile student,
            User openedBy,
            OnlineCourseEnrollmentStatus status
    ) {
        OnlineCourse course = findActiveCourse(courseId);
        if (!OnlineCourseStatus.PUBLISHED.equals(course.getStatus()))
            throw new CustomBadRequestException("Only published online courses can be enrolled");

        enrollmentRepository.findByCourseIdAndStudentIdAndDeletedFalse(courseId, student.getId())
                .ifPresent(existing -> {
                    throw new CustomConflictException("Student is already enrolled in this online course");
                });

        LocalDateTime now = LocalDateTime.now();
        OnlineCourseEnrollment enrollment = OnlineCourseEnrollment.builder()
                .course(course)
                .student(student)
                .openedBy(openedBy)
                .status(status)
                .openedAt(now)
                .build();

        initializeProgress(enrollment, now);
        return enrollmentRepository.save(enrollment);
    }

    private void initializeProgress(OnlineCourseEnrollment enrollment, LocalDateTime now) {
        List<OnlineCourseModule> modules = publishedAvailableModules(enrollment.getCourse());
        OnlineCourseUnlockStrategy strategy = enrollment.getCourse().getUnlockStrategy();

        for (int moduleIndex = 0; moduleIndex < modules.size(); moduleIndex++) {
            OnlineCourseModule module = modules.get(moduleIndex);
            boolean moduleAvailable = isInitialModuleAvailable(strategy, moduleIndex);
            OnlineCourseModuleProgress moduleProgress = OnlineCourseModuleProgress.builder()
                    .enrollment(enrollment)
                    .module(module)
                    .status(moduleAvailable ? OnlineCourseProgressStatus.AVAILABLE : OnlineCourseProgressStatus.LOCKED)
                    .openedAt(moduleAvailable ? now : null)
                    .build();
            enrollment.getModuleProgresses().add(moduleProgress);

            List<OnlineCourseLesson> lessons = publishedAvailableLessons(module);
            for (int lessonIndex = 0; lessonIndex < lessons.size(); lessonIndex++) {
                boolean lessonAvailable = isInitialLessonAvailable(strategy, moduleIndex, lessonIndex, moduleAvailable);
                OnlineCourseLesson lesson = lessons.get(lessonIndex);
                OnlineCourseLessonProgress lessonProgress = OnlineCourseLessonProgress.builder()
                        .enrollment(enrollment)
                        .lesson(lesson)
                        .status(lessonAvailable ? OnlineCourseProgressStatus.AVAILABLE : OnlineCourseProgressStatus.LOCKED)
                        .openedAt(lessonAvailable ? now : null)
                        .build();
                enrollment.getLessonProgresses().add(lessonProgress);
                if (lessonAvailable && enrollment.getCurrentLesson() == null) {
                    enrollment.setCurrentModule(module);
                    enrollment.setCurrentLesson(lesson);
                }
            }
        }
    }

    private boolean isInitialModuleAvailable(OnlineCourseUnlockStrategy strategy, int moduleIndex) {
        return OnlineCourseUnlockStrategy.ALL_AT_ONCE.equals(strategy) || moduleIndex == 0;
    }

    private boolean isInitialLessonAvailable(
            OnlineCourseUnlockStrategy strategy,
            int moduleIndex,
            int lessonIndex,
            boolean moduleAvailable
    ) {
        if (OnlineCourseUnlockStrategy.ALL_AT_ONCE.equals(strategy))
            return true;

        if (OnlineCourseUnlockStrategy.MODULE_BY_MODULE.equals(strategy))
            return moduleAvailable;

        return moduleIndex == 0 && lessonIndex == 0;
    }

    private void applyLessonProgress(OnlineCourseLessonProgress progress, OnlineCourseLessonProgressRequest request) {
        LocalDateTime now = LocalDateTime.now();
        progress.setLastPositionInSeconds(request.getLastPositionInSeconds());

        if (OnlineCourseProgressStatus.IN_PROGRESS.equals(request.getStatus()) && progress.getStartedAt() == null)
            progress.setStartedAt(now);

        if (OnlineCourseProgressStatus.COMPLETED.equals(request.getStatus())) {
            if (progress.getStartedAt() == null)
                progress.setStartedAt(now);
            progress.setCompletedAt(now);
        }

        progress.setStatus(request.getStatus());
        lessonProgressRepository.save(progress);
    }

    private void refreshModuleProgress(OnlineCourseEnrollment enrollment, OnlineCourseModule module) {
        OnlineCourseModuleProgress moduleProgress = moduleProgressRepository.findByEnrollmentIdAndModuleIdAndDeletedFalse(enrollment.getId(), module.getId())
                .orElse(null);
        if (moduleProgress == null)
            return;

        List<OnlineCourseLessonProgress> moduleLessonProgresses = enrollment.getLessonProgresses().stream()
                .filter(progress -> !progress.isDeleted())
                .filter(progress -> progress.getLesson().getModule().getId().equals(module.getId()))
                .toList();

        if (moduleLessonProgresses.isEmpty())
            return;

        boolean allCompleted = moduleLessonProgresses.stream()
                .allMatch(progress -> OnlineCourseProgressStatus.COMPLETED.equals(progress.getStatus()));
        boolean anyStarted = moduleLessonProgresses.stream()
                .anyMatch(progress -> OnlineCourseProgressStatus.IN_PROGRESS.equals(progress.getStatus())
                        || OnlineCourseProgressStatus.COMPLETED.equals(progress.getStatus()));

        if (allCompleted) {
            moduleProgress.setStatus(OnlineCourseProgressStatus.COMPLETED);
            moduleProgress.setCompletedAt(LocalDateTime.now());
        } else if (anyStarted) {
            moduleProgress.setStatus(OnlineCourseProgressStatus.IN_PROGRESS);
            if (moduleProgress.getStartedAt() == null)
                moduleProgress.setStartedAt(LocalDateTime.now());
        }

        moduleProgressRepository.save(moduleProgress);
    }

    private void unlockNextContent(OnlineCourseEnrollment enrollment) {
        if (OnlineCourseUnlockStrategy.ALL_AT_ONCE.equals(enrollment.getCourse().getUnlockStrategy()))
            return;

        if (OnlineCourseUnlockStrategy.MODULE_BY_MODULE.equals(enrollment.getCourse().getUnlockStrategy())) {
            unlockNextModule(enrollment);
            return;
        }

        unlockNextLesson(enrollment);
    }

    private void unlockNextModule(OnlineCourseEnrollment enrollment) {
        List<OnlineCourseModuleProgress> sortedModules = enrollment.getModuleProgresses().stream()
                .filter(progress -> !progress.isDeleted())
                .sorted(Comparator.comparing(progress -> progress.getModule().getOrderIndex()))
                .toList();

        for (int i = 0; i < sortedModules.size() - 1; i++) {
            OnlineCourseModuleProgress current = sortedModules.get(i);
            OnlineCourseModuleProgress next = sortedModules.get(i + 1);
            if (OnlineCourseProgressStatus.COMPLETED.equals(current.getStatus())
                    && OnlineCourseProgressStatus.LOCKED.equals(next.getStatus())) {
                unlockModuleWithLessons(enrollment, next.getModule());
                return;
            }
        }
    }

    private void unlockModuleWithLessons(OnlineCourseEnrollment enrollment, OnlineCourseModule module) {
        LocalDateTime now = LocalDateTime.now();
        moduleProgressRepository.findByEnrollmentIdAndModuleIdAndDeletedFalse(enrollment.getId(), module.getId())
                .ifPresent(progress -> {
                    progress.setStatus(OnlineCourseProgressStatus.AVAILABLE);
                    progress.setOpenedAt(now);
                    moduleProgressRepository.save(progress);
                });

        enrollment.getLessonProgresses().stream()
                .filter(progress -> !progress.isDeleted())
                .filter(progress -> progress.getLesson().getModule().getId().equals(module.getId()))
                .filter(progress -> OnlineCourseProgressStatus.LOCKED.equals(progress.getStatus()))
                .forEach(progress -> {
                    progress.setStatus(OnlineCourseProgressStatus.AVAILABLE);
                    progress.setOpenedAt(now);
                    lessonProgressRepository.save(progress);
                });
    }

    private void unlockNextLesson(OnlineCourseEnrollment enrollment) {
        List<OnlineCourseLessonProgress> sortedLessons = enrollment.getLessonProgresses().stream()
                .filter(progress -> !progress.isDeleted())
                .sorted(Comparator
                        .comparing((OnlineCourseLessonProgress progress) -> progress.getLesson().getModule().getOrderIndex())
                        .thenComparing(progress -> progress.getLesson().getOrderIndex()))
                .toList();

        for (int i = 0; i < sortedLessons.size() - 1; i++) {
            OnlineCourseLessonProgress current = sortedLessons.get(i);
            OnlineCourseLessonProgress next = sortedLessons.get(i + 1);
            if (OnlineCourseProgressStatus.COMPLETED.equals(current.getStatus())
                    && OnlineCourseProgressStatus.LOCKED.equals(next.getStatus())) {
                LocalDateTime now = LocalDateTime.now();
                next.setStatus(OnlineCourseProgressStatus.AVAILABLE);
                next.setOpenedAt(now);
                lessonProgressRepository.save(next);
                unlockModuleIfNeeded(enrollment, next.getLesson().getModule(), now);
                return;
            }
        }
    }

    private void unlockModuleIfNeeded(OnlineCourseEnrollment enrollment, OnlineCourseModule module, LocalDateTime now) {
        moduleProgressRepository.findByEnrollmentIdAndModuleIdAndDeletedFalse(enrollment.getId(), module.getId())
                .filter(progress -> OnlineCourseProgressStatus.LOCKED.equals(progress.getStatus()))
                .ifPresent(progress -> {
                    progress.setStatus(OnlineCourseProgressStatus.AVAILABLE);
                    progress.setOpenedAt(now);
                    moduleProgressRepository.save(progress);
                });
    }

    private void completeEnrollmentIfReady(OnlineCourseEnrollment enrollment) {
        boolean allCompleted = !enrollment.getLessonProgresses().isEmpty()
                && enrollment.getLessonProgresses().stream()
                .filter(progress -> !progress.isDeleted())
                .allMatch(progress -> OnlineCourseProgressStatus.COMPLETED.equals(progress.getStatus()));

        if (allCompleted) {
            enrollment.setStatus(OnlineCourseEnrollmentStatus.COMPLETED);
            enrollment.setCompletedAt(LocalDateTime.now());
        }
    }

    private List<OnlineCourseModule> publishedAvailableModules(OnlineCourse course) {
        if (course.getModules() == null)
            return List.of();

        return course.getModules().stream()
                .filter(module -> !module.isDeleted())
                .filter(module -> OnlineCourseContentStatus.PUBLISHED.equals(module.getStatus()))
                .filter(module -> module.getAvailableFrom() == null || !module.getAvailableFrom().isAfter(LocalDateTime.now()))
                .sorted(Comparator.comparing(OnlineCourseModule::getOrderIndex))
                .toList();
    }

    private List<OnlineCourseLesson> publishedAvailableLessons(OnlineCourseModule module) {
        if (module.getLessons() == null)
            return List.of();

        return module.getLessons().stream()
                .filter(lesson -> !lesson.isDeleted())
                .filter(lesson -> OnlineCourseContentStatus.PUBLISHED.equals(lesson.getStatus()))
                .filter(lesson -> lesson.getAvailableFrom() == null || !lesson.getAvailableFrom().isAfter(LocalDateTime.now()))
                .sorted(Comparator.comparing(OnlineCourseLesson::getOrderIndex))
                .toList();
    }

    private OnlineCourse findActiveCourse(UUID courseId) {
        return courseRepository.findByIdAndDeletedFalse(courseId)
                .orElseThrow(() -> new CustomNotFoundException("Online course not found with id: " + courseId));
    }

    private OnlineCourseModule findActiveModule(UUID moduleId) {
        return moduleRepository.findByIdAndDeletedFalse(moduleId)
                .orElseThrow(() -> new CustomNotFoundException("Online course module not found with id: " + moduleId));
    }

    private OnlineCourseLesson findActiveLesson(UUID lessonId) {
        return lessonRepository.findByIdAndDeletedFalse(lessonId)
                .orElseThrow(() -> new CustomNotFoundException("Online course lesson not found with id: " + lessonId));
    }

    private OnlineCourseLessonMaterial findActiveMaterial(UUID materialId) {
        return materialRepository.findByIdAndDeletedFalse(materialId)
                .orElseThrow(() -> new CustomNotFoundException("Online course lesson material not found with id: " + materialId));
    }

    private StudentProfile findActiveStudent(UUID studentProfileId) {
        return studentProfileRepository.findByIdAndDeletedFalse(studentProfileId)
                .orElseThrow(() -> new CustomNotFoundException("Student profile not found with id: " + studentProfileId));
    }

    private StudentProfile findStudentByUser(UUID userId) {
        return studentProfileRepository.findByUserId(userId)
                .filter(profile -> !profile.isDeleted())
                .orElseThrow(() -> new CustomNotFoundException("Student profile not found for current user"));
    }

    private Attachment findOptionalAttachment(UUID attachmentId, String message) {
        if (attachmentId == null)
            return null;

        return findRequiredAttachment(attachmentId, message);
    }

    private Attachment findRequiredAttachment(UUID attachmentId, String message) {
        return attachmentRepository.findByIdAndDeletedFalse(attachmentId)
                .orElseThrow(() -> new CustomNotFoundException(message + ": " + attachmentId));
    }

    private void ensureSlugAvailable(String slug, UUID courseId) {
        boolean exists = courseId == null
                ? courseRepository.existsBySlug(slug)
                : courseRepository.existsBySlugAndIdNot(slug, courseId);

        if (exists)
            throw new CustomConflictException("Online course slug already exists: " + slug);
    }

    private void ensureModuleOrderAvailable(UUID courseId, Integer orderIndex, UUID moduleId) {
        boolean exists = moduleId == null
                ? moduleRepository.existsByCourseIdAndOrderIndexAndDeletedFalse(courseId, orderIndex)
                : moduleRepository.existsByCourseIdAndOrderIndexAndIdNotAndDeletedFalse(courseId, orderIndex, moduleId);

        if (exists)
            throw new CustomConflictException("Module order index already exists in this course: " + orderIndex);
    }

    private void ensureLessonOrderAvailable(UUID moduleId, Integer orderIndex, UUID lessonId) {
        boolean exists = lessonId == null
                ? lessonRepository.existsByModuleIdAndOrderIndexAndDeletedFalse(moduleId, orderIndex)
                : lessonRepository.existsByModuleIdAndOrderIndexAndIdNotAndDeletedFalse(moduleId, orderIndex, lessonId);

        if (exists)
            throw new CustomConflictException("Lesson order index already exists in this module: " + orderIndex);
    }

    private void assertCanViewCourse(OnlineCourse course, CustomUserDetails details) {
        if (OnlineCourseStatus.PUBLISHED.equals(course.getStatus()) || canManageCourse(course, details))
            return;

        throw new AccessDeniedException("You do not have access to this online course");
    }

    private void assertCanViewStudent(UUID studentProfileId, CustomUserDetails details) {
        if (canManageAll(details))
            return;

        if (hasRole(details, ROLE_STUDENT) && findStudentByUser(details.getId()).getId().equals(studentProfileId))
            return;

        if ((hasRole(details, ROLE_PARENT) || hasRole(details, ROLE_GUARDIAN))
                && parentProfileRepository.existsActiveStudentLink(details.getId(), studentProfileId))
            return;

        throw new AccessDeniedException("You do not have access to this student profile");
    }

    private void assertCanAuthor(CustomUserDetails details) {
        if (!canAuthor(details))
            throw new AccessDeniedException("You do not have access to author online courses");
    }

    private void assertCanManageAll(CustomUserDetails details) {
        if (!canManageAll(details))
            throw new AccessDeniedException("You do not have access to manage all online courses");
    }

    private void assertCanManageCourse(OnlineCourse course, CustomUserDetails details) {
        if (!canManageCourse(course, details))
            throw new AccessDeniedException("You do not have access to manage this online course");
    }

    private void assertHasRole(CustomUserDetails details, String role) {
        if (!hasRole(details, role))
            throw new AccessDeniedException("Required role is missing: " + role);
    }

    private boolean canManageCourse(OnlineCourse course, CustomUserDetails details) {
        return canManageAll(details)
                || (canAuthor(details)
                && course.getCreatedBy() != null
                && Objects.equals(course.getCreatedBy().getId(), details.getId()));
    }

    private boolean canManageAll(CustomUserDetails details) {
        return hasAnyRole(details, ROLE_SUPER_ADMIN, ROLE_ADMIN, ROLE_MAINTAINER);
    }

    private boolean canAuthor(CustomUserDetails details) {
        return hasAnyRole(details, ROLE_SUPER_ADMIN, ROLE_ADMIN, ROLE_MAINTAINER, ROLE_TEACHER, ROLE_SUPPORT_TEACHER);
    }

    private boolean hasAnyRole(CustomUserDetails details, String... roles) {
        for (String role : roles) {
            if (hasRole(details, role))
                return true;
        }
        return false;
    }

    private boolean hasRole(CustomUserDetails details, String role) {
        return details.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role::equals);
    }

    private void validateProgressStatus(OnlineCourseProgressStatus status) {
        if (OnlineCourseProgressStatus.LOCKED.equals(status))
            throw new CustomBadRequestException("Progress cannot be manually set to LOCKED");
    }

    private void validateDurationRange(Integer minDurationInMinutes, Integer maxDurationInMinutes) {
        if (minDurationInMinutes != null && minDurationInMinutes <= 0)
            throw new CustomBadRequestException("minDuration must be greater than 0");

        if (maxDurationInMinutes != null && maxDurationInMinutes <= 0)
            throw new CustomBadRequestException("maxDuration must be greater than 0");

        if (minDurationInMinutes != null && maxDurationInMinutes != null && minDurationInMinutes > maxDurationInMinutes)
            throw new CustomBadRequestException("minDuration must be less than or equal to maxDuration");
    }

    private String normalizeNullable(String value) {
        if (value == null)
            return null;

        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    private String normalizeSlug(String requestedSlug, String title) {
        String source = requestedSlug == null || requestedSlug.isBlank() ? title : requestedSlug;
        String normalized = Normalizer.normalize(source.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");

        if (normalized.isBlank())
            throw new CustomBadRequestException("Slug must contain at least one letter or number");

        if (normalized.length() > 255)
            return normalized.substring(0, 255).replaceAll("-$", "");

        return normalized;
    }

    private void softDelete(BaseEntity entity) {
        entity.setDeleted(true);
        entity.setDeletedAt(LocalDateTime.now());
    }
}
