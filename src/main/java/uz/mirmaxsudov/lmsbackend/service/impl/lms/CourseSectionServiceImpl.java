package uz.mirmaxsudov.lmsbackend.service.impl.lms;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.mirmaxsudov.lmsbackend.common.filter.PageableBuilder;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.CourseSectionMapper;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomNotFoundException;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Course;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.CourseSection;
import uz.mirmaxsudov.lmsbackend.model.request.lms.CourseSectionCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.CourseSectionUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.CourseSectionResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.LessonResponse;
import uz.mirmaxsudov.lmsbackend.repository.lms.course.CourseRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.section.CourseSectionFilter;
import uz.mirmaxsudov.lmsbackend.repository.lms.section.CourseSectionRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.section.CourseSectionSpecification;
import uz.mirmaxsudov.lmsbackend.service.base.lms.CourseSectionService;
import uz.mirmaxsudov.lmsbackend.service.impl.BaseCRUDServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CourseSectionServiceImpl extends BaseCRUDServiceImpl<CourseSection, CourseSectionRepository> implements CourseSectionService {

    private final CourseRepository courseRepository;

    public CourseSectionServiceImpl(CourseSectionRepository repository, CourseRepository courseRepository) {
        super(repository);
        this.courseRepository = courseRepository;
    }

    @Override
    public ResponseEntity<ApiPaginateResponse<List<CourseSectionResponse>>> getAll(
            int page,
            int size,
            String search,
            UUID courseId,
            Integer orderIndex
    ) {
        int normalizedPage = Math.max(page - 1, 0);
        int normalizedSize = size <= 0 ? 10 : size;

        Pageable pageable = PageableBuilder.build(normalizedPage, normalizedSize);
        Specification<CourseSection> filter = CourseSectionSpecification.filter(CourseSectionFilter.builder()
                .search(search)
                .courseId(courseId)
                .orderIndex(orderIndex)
                .build());

        Page<CourseSection> sections = repository.findAll(filter, pageable);
        List<CourseSectionResponse> results = sections.getContent().stream()
                .map(CourseSectionMapper::toResponse)
                .toList();

        return ResponseEntity.ok(ApiPaginateResponse.<List<CourseSectionResponse>>builder()
                .success(true)
                .message("Course sections fetched successfully")
                .results(results)
                .total((int) sections.getTotalElements())
                .page(sections.getNumber() + 1)
                .size(sections.getSize())
                .hasNext(sections.hasNext())
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<CourseSectionResponse>> getByIdResponse(UUID id) {
        CourseSection section = findActiveSection(id);

        return ResponseEntity.ok(ApiResponse.<CourseSectionResponse>builder()
                .success(true)
                .message("Course section fetched successfully")
                .data(CourseSectionMapper.toResponse(section))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<CourseSectionResponse>> createSection(CourseSectionCreateRequest request) {
        Course course = courseRepository.findByIdAndDeletedFalse(request.getCourseId())
                .orElseThrow(() -> new CustomNotFoundException("Course not found with id: " + request.getCourseId()));

        CourseSection section = CourseSection.builder()
                .title(request.getTitle().trim())
                .course(course)
                .orderIndex(request.getOrderIndex())
                .build();

        CourseSection savedSection = repository.save(section);

        return ResponseEntity.ok(ApiResponse.<CourseSectionResponse>builder()
                .success(true)
                .message("Course section created successfully")
                .data(CourseSectionMapper.toResponse(savedSection))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<CourseSectionResponse>> updateSection(UUID id, CourseSectionUpdateRequest request) {
        CourseSection existingSection = findActiveSection(id);

        existingSection.setTitle(request.getTitle().trim());
        existingSection.setOrderIndex(request.getOrderIndex());

        CourseSection updatedSection = repository.save(existingSection);

        return ResponseEntity.ok(ApiResponse.<CourseSectionResponse>builder()
                .success(true)
                .message("Course section updated successfully")
                .data(CourseSectionMapper.toResponse(updatedSection))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteSection(UUID id) {
        CourseSection existingSection = findActiveSection(id);

        existingSection.setDeleted(true);
        existingSection.setDeletedAt(LocalDateTime.now());
        repository.save(existingSection);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Course section deleted successfully")
                .build());
    }

    private CourseSection findActiveSection(UUID id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new CustomNotFoundException("Course section not found with id: " + id));
    }
}
