package uz.mirmaxsudov.lmsbackend.service.impl.lms;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.mirmaxsudov.lmsbackend.common.filter.PageableBuilder;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.CourseMapper;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomBadRequestException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomNotFoundException;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Course;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.CourseLevel;
import uz.mirmaxsudov.lmsbackend.model.request.lms.CourseCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.CourseUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.CourseResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.CourseSectionResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.LessonResponse;
import uz.mirmaxsudov.lmsbackend.repository.lms.course.CourseFilter;
import uz.mirmaxsudov.lmsbackend.repository.lms.course.CourseRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.course.CourseSpecification;
import uz.mirmaxsudov.lmsbackend.service.base.lms.CourseService;
import uz.mirmaxsudov.lmsbackend.service.impl.BaseCRUDServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CourseServiceImpl extends BaseCRUDServiceImpl<Course, CourseRepository> implements CourseService {
    public CourseServiceImpl(CourseRepository repository) {
        super(repository);
    }

    @Override
    public ResponseEntity<ApiPaginateResponse<List<CourseResponse>>> getAll(
            int page,
            int size,
            String search,
            CourseLevel level,
            Integer minDurationInMinutes,
            Integer maxDurationInMinutes
    ) {
        int normalizedPage = Math.max(page - 1, 0);
        int normalizedSize = size <= 0 ? 10 : size;
        validateDurationRange(minDurationInMinutes, maxDurationInMinutes);

        Pageable pageable = PageableBuilder.build(normalizedPage, normalizedSize);
        Specification<Course> filter = CourseSpecification.filter(CourseFilter.builder()
                .search(search)
                .level(level)
                .minDurationInMinutes(minDurationInMinutes)
                .maxDurationInMinutes(maxDurationInMinutes)
                .build());

        Page<Course> courses = repository.findAll(filter, pageable);
        List<CourseResponse> results = courses.getContent().stream()
                .map(CourseMapper::toResponse)
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

    @Override
    public ResponseEntity<ApiResponse<CourseResponse>> getByIdResponse(UUID id) {
        Course course = findActiveCourse(id);

        return ResponseEntity.ok(ApiResponse.<CourseResponse>builder()
                .success(true)
                .message("Course fetched successfully")
                .data(CourseMapper.toResponse(course))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(CourseCreateRequest request) {
        Course course = Course.builder()
                .title(request.getTitle().trim())
                .description(normalizeDescription(request.getDescription()))
                .level(request.getLevel())
                .durationInMinutes(request.getDurationInMinutes())
                .build();

        Course savedCourse = repository.save(course);

        return ResponseEntity.ok(ApiResponse.<CourseResponse>builder()
                .success(true)
                .message("Course created successfully")
                .data(CourseMapper.toResponse(savedCourse))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(UUID id, CourseUpdateRequest request) {
        Course existingCourse = findActiveCourse(id);

        existingCourse.setTitle(request.getTitle().trim());
        existingCourse.setDescription(normalizeDescription(request.getDescription()));
        existingCourse.setLevel(request.getLevel());
        existingCourse.setDurationInMinutes(request.getDurationInMinutes());

        Course updatedCourse = repository.save(existingCourse);

        return ResponseEntity.ok(ApiResponse.<CourseResponse>builder()
                .success(true)
                .message("Course updated successfully")
                .data(CourseMapper.toResponse(updatedCourse))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteCourse(UUID id) {
        Course existingCourse = findActiveCourse(id);

        existingCourse.setDeleted(true);
        existingCourse.setDeletedAt(LocalDateTime.now());
        repository.save(existingCourse);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Course deleted successfully")
                .build());
    }

    private Course findActiveCourse(UUID id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new CustomNotFoundException("Course not found with id: " + id));
    }

    private String normalizeDescription(String description) {
        if (description == null)
            return null;

        String trimmedDescription = description.trim();
        return trimmedDescription.isBlank() ? null : trimmedDescription;
    }

    private void validateDurationRange(Integer minDurationInMinutes, Integer maxDurationInMinutes) {
        if (minDurationInMinutes != null && minDurationInMinutes <= 0)
            throw new CustomBadRequestException("minDuration must be greater than 0");

        if (maxDurationInMinutes != null && maxDurationInMinutes <= 0)
            throw new CustomBadRequestException("maxDuration must be greater than 0");

        if (minDurationInMinutes != null
                && maxDurationInMinutes != null
                && minDurationInMinutes > maxDurationInMinutes)
            throw new CustomBadRequestException("minDuration must be less than or equal to maxDuration");
    }
}
