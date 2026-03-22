package uz.mirmaxsudov.lmsbackend.service.impl.lms;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import uz.mirmaxsudov.lmsbackend.common.filter.PageableBuilder;
import uz.mirmaxsudov.lmsbackend.common.util.lms.LmsAccessControl;
import uz.mirmaxsudov.lmsbackend.common.util.lms.LmsResponseMapper;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomConflictException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomNotFoundException;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Course;
import uz.mirmaxsudov.lmsbackend.model.request.lms.CourseCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.CourseUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.CourseResponse;
import uz.mirmaxsudov.lmsbackend.repository.UserRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.CourseRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.specification.CourseSpecification;
import uz.mirmaxsudov.lmsbackend.repository.lms.specification.dto.CourseFilter;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;
import uz.mirmaxsudov.lmsbackend.service.base.lms.CourseService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<ApiResponse<CourseResponse>> create(CourseCreateRequest request, CustomUserDetails details) {
        User currentUser = details.user();
        LmsAccessControl.requireWriteRole(currentUser);

        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new CustomNotFoundException("Teacher not found"));

        if (!LmsAccessControl.isAdmin(currentUser) && !Objects.equals(currentUser.getId(), teacher.getId())) {
            throw new AccessDeniedException("Teacher can only create own course");
        }

        if (courseRepository.existsByCodeIgnoreCase(request.getCode())) {
            throw new CustomConflictException("Course code already exists: " + request.getCode());
        }

        Course course = Course.builder()
                .name(request.getName())
                .code(request.getCode())
                .description(request.getDescription())
                .isActive(request.getActive() == null || request.getActive())
                .teacher(teacher)
                .build();

        Course saved = courseRepository.save(course);

        return ResponseEntity.ok(ApiResponse.<CourseResponse>builder()
                .success(true)
                .message("Course created successfully")
                .data(LmsResponseMapper.toCourseResponse(saved))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<CourseResponse>> getById(UUID id, CustomUserDetails details) {
        User currentUser = details.user();
        LmsAccessControl.requireReadRole(currentUser);

        Course course = findVisibleById(id, currentUser);

        return ResponseEntity.ok(ApiResponse.<CourseResponse>builder()
                .success(true)
                .message("Course fetched successfully")
                .data(LmsResponseMapper.toCourseResponse(course))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<CourseResponse>> update(UUID id, CourseUpdateRequest request, CustomUserDetails details) {
        User currentUser = details.user();
        LmsAccessControl.requireWriteRole(currentUser);

        Course course = findWritableById(id, currentUser);

        if (courseRepository.existsByCodeIgnoreCaseAndIdNot(request.getCode(), id)) {
            throw new CustomConflictException("Course code already exists: " + request.getCode());
        }

        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new CustomNotFoundException("Teacher not found"));

        if (!LmsAccessControl.isAdmin(currentUser) && !Objects.equals(currentUser.getId(), teacher.getId())) {
            throw new AccessDeniedException("Teacher can only assign own course");
        }

        course.setName(request.getName());
        course.setCode(request.getCode());
        course.setDescription(request.getDescription());
        course.setActive(request.getActive());
        course.setTeacher(teacher);

        Course updated = courseRepository.save(course);

        return ResponseEntity.ok(ApiResponse.<CourseResponse>builder()
                .success(true)
                .message("Course updated successfully")
                .data(LmsResponseMapper.toCourseResponse(updated))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> delete(UUID id, CustomUserDetails details) {
        User currentUser = details.user();
        LmsAccessControl.requireWriteRole(currentUser);

        Course course = findWritableById(id, currentUser);
        courseRepository.delete(course);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Course deleted successfully")
                .build());
    }

    @Override
    public ResponseEntity<ApiPaginateResponse<List<CourseResponse>>> getPaginate(int page, int size, String search, UUID teacherId, Boolean active, CustomUserDetails details) {
        User currentUser = details.user();
        LmsAccessControl.requireReadRole(currentUser);

        int normalizedPage = Math.max(page - 1, 0);
        int normalizedSize = size <= 0 ? 10 : size;

        Pageable pageable = PageableBuilder.build(normalizedPage, normalizedSize);
        Specification<Course> specification = CourseSpecification.filter(CourseFilter.builder()
                .search(search)
                .teacherId(teacherId)
                .active(active)
                .build());

        specification = applyReadVisibility(specification, currentUser);

        Page<Course> courses = courseRepository.findAll(specification, pageable);
        List<CourseResponse> results = courses.getContent().stream()
                .map(LmsResponseMapper::toCourseResponse)
                .toList();

        return ResponseEntity.ok(ApiPaginateResponse.<List<CourseResponse>>builder()
                .success(true)
                .message("Courses fetched successfully")
                .results(results)
                .total((int) courses.getTotalElements())
                .page(courses.getNumber() + 1)
                .size(courses.getSize())
                .hasNext(courses.hasNext())
                .build());
    }

    private Course findVisibleById(UUID id, User currentUser) {
        Specification<Course> byId = (root, query, cb) -> cb.equal(root.get("id"), id);
        Specification<Course> specification = applyReadVisibility(byId, currentUser);
        return courseRepository.findOne(specification)
                .orElseThrow(() -> new CustomNotFoundException("Course not found"));
    }

    private Course findWritableById(UUID id, User currentUser) {
        if (LmsAccessControl.isAdmin(currentUser)) {
            return courseRepository.findById(id)
                    .orElseThrow(() -> new CustomNotFoundException("Course not found"));
        }

        if (LmsAccessControl.isTeacher(currentUser)) {
            return courseRepository.findByIdAndTeacherId(id, currentUser.getId())
                    .orElseThrow(() -> new CustomNotFoundException("Course not found"));
        }

        throw new AccessDeniedException("Only admin or teacher can modify course");
    }

    private Specification<Course> applyReadVisibility(Specification<Course> specification, User currentUser) {
        Specification<Course> result = Specification.where(specification);

        if (LmsAccessControl.isAdmin(currentUser)) {
            return result;
        }

        if (LmsAccessControl.isTeacher(currentUser)) {
            return result.and((root, query, cb) -> cb.equal(root.get("teacher").get("id"), currentUser.getId()));
        }

        if (LmsAccessControl.isStudent(currentUser)) {
            return result.and(CourseSpecification.visibleForStudent(currentUser.getId()));
        }

        if (LmsAccessControl.isParent(currentUser)) {
            return result.and(CourseSpecification.visibleForParent(currentUser.getId()));
        }

        throw new AccessDeniedException("You do not have permission to view courses");
    }
}
