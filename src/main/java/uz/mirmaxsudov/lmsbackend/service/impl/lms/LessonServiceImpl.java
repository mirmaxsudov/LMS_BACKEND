package uz.mirmaxsudov.lmsbackend.service.impl.lms;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.mirmaxsudov.lmsbackend.common.filter.PageableBuilder;
import uz.mirmaxsudov.lmsbackend.common.util.mappers.LessonMapper;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomNotFoundException;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.CourseSection;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Lesson;
import uz.mirmaxsudov.lmsbackend.model.request.lms.LessonCreateRequest;
import uz.mirmaxsudov.lmsbackend.model.request.lms.LessonUpdateRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.LessonResponse;
import uz.mirmaxsudov.lmsbackend.repository.lms.lesson.LessonFilter;
import uz.mirmaxsudov.lmsbackend.repository.lms.lesson.LessonRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.lesson.LessonSpecification;
import uz.mirmaxsudov.lmsbackend.repository.lms.section.CourseSectionRepository;
import uz.mirmaxsudov.lmsbackend.service.base.lms.LessonService;
import uz.mirmaxsudov.lmsbackend.service.impl.BaseCRUDServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LessonServiceImpl extends BaseCRUDServiceImpl<Lesson, LessonRepository> implements LessonService {

    private final CourseSectionRepository sectionRepository;

    public LessonServiceImpl(LessonRepository repository, CourseSectionRepository sectionRepository) {
        super(repository);
        this.sectionRepository = sectionRepository;
    }

    @Override
    public ResponseEntity<ApiPaginateResponse<List<LessonResponse>>> getAll(
            int page,
            int size,
            String search,
            UUID sectionId,
            Integer minDuration,
            Integer maxDuration
    ) {
        int normalizedPage = Math.max(page - 1, 0);
        int normalizedSize = size <= 0 ? 10 : size;

        Pageable pageable = PageableBuilder.build(normalizedPage, normalizedSize);
        Specification<Lesson> filter = LessonSpecification.filter(LessonFilter.builder()
                .search(search)
                .sectionId(sectionId)
                .minDuration(minDuration)
                .maxDuration(maxDuration)
                .build());

        Page<Lesson> lessons = repository.findAll(filter, pageable);
        List<LessonResponse> results = lessons.getContent().stream()
                .map(LessonMapper::toResponse)
                .toList();

        return ResponseEntity.ok(ApiPaginateResponse.<List<LessonResponse>>builder()
                .success(true)
                .message("Lessons fetched successfully")
                .results(results)
                .total((int) lessons.getTotalElements())
                .page(lessons.getNumber() + 1)
                .size(lessons.getSize())
                .hasNext(lessons.hasNext())
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<LessonResponse>> getByIdResponse(UUID id) {
        Lesson lesson = findActiveLesson(id);

        return ResponseEntity.ok(ApiResponse.<LessonResponse>builder()
                .success(true)
                .message("Lesson fetched successfully")
                .data(LessonMapper.toResponse(lesson))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<LessonResponse>> createLesson(LessonCreateRequest request) {
        CourseSection section = sectionRepository.findByIdAndDeletedFalse(request.getSectionId())
                .orElseThrow(() -> new CustomNotFoundException("Course section not found with id: " + request.getSectionId()));

        Lesson lesson = Lesson.builder()
                .title(request.getTitle().trim())
                .content(request.getContent())
                .durationInMinutes(request.getDurationInMinutes())
                .section(section)
                .build();

        Lesson savedLesson = repository.save(lesson);

        return ResponseEntity.ok(ApiResponse.<LessonResponse>builder()
                .success(true)
                .message("Lesson created successfully")
                .data(LessonMapper.toResponse(savedLesson))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<LessonResponse>> updateLesson(UUID id, LessonUpdateRequest request) {
        Lesson existingLesson = findActiveLesson(id);

        existingLesson.setTitle(request.getTitle().trim());
        existingLesson.setContent(request.getContent());
        existingLesson.setDurationInMinutes(request.getDurationInMinutes());

        Lesson updatedLesson = repository.save(existingLesson);

        return ResponseEntity.ok(ApiResponse.<LessonResponse>builder()
                .success(true)
                .message("Lesson updated successfully")
                .data(LessonMapper.toResponse(updatedLesson))
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteLesson(UUID id) {
        Lesson existingLesson = findActiveLesson(id);

        existingLesson.setDeleted(true);
        existingLesson.setDeletedAt(LocalDateTime.now());
        repository.save(existingLesson);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Lesson deleted successfully")
                .build());
    }

    private Lesson findActiveLesson(UUID id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new CustomNotFoundException("Lesson not found with id: " + id));
    }
}
