package uz.mirmaxsudov.lmsbackend.service.impl.search;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomBadRequestException;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Course;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.CourseSection;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Lesson;
import uz.mirmaxsudov.lmsbackend.model.enums.search.SearchResultType;
import uz.mirmaxsudov.lmsbackend.model.response.ApiPaginateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.search.GlobalSearchResultResponse;
import uz.mirmaxsudov.lmsbackend.repository.lms.course.CourseRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.lesson.LessonRepository;
import uz.mirmaxsudov.lmsbackend.repository.lms.section.CourseSectionRepository;
import uz.mirmaxsudov.lmsbackend.repository.search.GlobalSearchSpecification;
import uz.mirmaxsudov.lmsbackend.service.base.search.GlobalSearchService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class GlobalSearchServiceImpl implements GlobalSearchService {
    private static final int DEFAULT_SIZE = 8;
    private static final int MAX_SIZE = 20;
    private static final int MAX_QUERY_LENGTH = 100;
    private static final int DESCRIPTION_LENGTH = 120;

    private final CourseRepository courseRepository;
    private final CourseSectionRepository sectionRepository;
    private final LessonRepository lessonRepository;

    public GlobalSearchServiceImpl(
            CourseRepository courseRepository,
            CourseSectionRepository sectionRepository,
            LessonRepository lessonRepository
    ) {
        this.courseRepository = courseRepository;
        this.sectionRepository = sectionRepository;
        this.lessonRepository = lessonRepository;
    }

    @Override
    public ResponseEntity<ApiPaginateResponse<List<GlobalSearchResultResponse>>> search(
            String query,
            Set<SearchResultType> types,
            int page,
            int size
    ) {
        String normalizedQuery = normalizeQuery(query);
        Set<SearchResultType> resolvedTypes = resolveTypes(types);
        int normalizedPage = Math.max(page, 1);
        int normalizedSize = normalizeSize(size);
        int offset = (normalizedPage - 1) * normalizedSize;
        int fetchLimit = offset + normalizedSize;

        String pattern = "%" + normalizedQuery.toLowerCase(Locale.ROOT) + "%";
        Pageable fetchPageable = PageRequest.of(0, fetchLimit);
        List<GlobalSearchResultResponse> mergedResults = new ArrayList<>();
        int total = 0;

        if (resolvedTypes.contains(SearchResultType.COURSE)) {
            Specification<Course> specification = GlobalSearchSpecification.course(pattern);
            total += Math.toIntExact(courseRepository.count(specification));
            mergedResults.addAll(searchCourses(normalizedQuery, specification, fetchPageable));
        }

        if (resolvedTypes.contains(SearchResultType.SECTION)) {
            Specification<CourseSection> specification = GlobalSearchSpecification.section(pattern);
            total += Math.toIntExact(sectionRepository.count(specification));
            mergedResults.addAll(searchSections(normalizedQuery, specification, fetchPageable));
        }

        if (resolvedTypes.contains(SearchResultType.LESSON)) {
            Specification<Lesson> specification = GlobalSearchSpecification.lesson(pattern);
            total += Math.toIntExact(lessonRepository.count(specification));
            mergedResults.addAll(searchLessons(normalizedQuery, specification, fetchPageable));
        }

        List<GlobalSearchResultResponse> sortedResults = mergedResults.stream()
                .sorted(Comparator
                        .comparingInt(GlobalSearchResultResponse::getScore).reversed()
                        .thenComparing(GlobalSearchResultResponse::getTitle, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(result -> result.getType().name()))
                .toList();

        List<GlobalSearchResultResponse> results = sortedResults.stream()
                .skip(offset)
                .limit(normalizedSize)
                .toList();

        return ResponseEntity.ok(ApiPaginateResponse.<List<GlobalSearchResultResponse>>builder()
                .success(true)
                .message("Search results fetched successfully")
                .results(results)
                .total(total)
                .page(normalizedPage)
                .size(normalizedSize)
                .hasNext(offset + results.size() < total)
                .build());
    }

    private List<GlobalSearchResultResponse> searchCourses(
            String query,
            Specification<Course> specification,
            Pageable pageable
    ) {
        List<Course> courses = courseRepository.findAll(specification, pageable).getContent();
        return courses.stream()
                .map(course -> GlobalSearchResultResponse.builder()
                        .id(course.getId())
                        .type(SearchResultType.COURSE)
                        .title(course.getTitle())
                        .subtitle(course.getLevel().name() + " course")
                        .description(snippet(course.getDescription()))
                        .url("/courses/" + course.getId())
                        .score(score(query, course.getTitle(), course.getDescription()))
                        .build())
                .toList();
    }

    private List<GlobalSearchResultResponse> searchSections(
            String query,
            Specification<CourseSection> specification,
            Pageable pageable
    ) {
        List<CourseSection> sections = sectionRepository.findAll(specification, pageable).getContent();
        return sections.stream()
                .map(section -> GlobalSearchResultResponse.builder()
                        .id(section.getId())
                        .type(SearchResultType.SECTION)
                        .title(section.getTitle())
                        .subtitle(section.getCourse().getTitle())
                        .description("Section " + section.getOrderIndex())
                        .url("/courses/" + section.getCourse().getId() + "/sections/" + section.getId())
                        .score(score(query, section.getTitle(), section.getCourse().getTitle()))
                        .build())
                .toList();
    }

    private List<GlobalSearchResultResponse> searchLessons(
            String query,
            Specification<Lesson> specification,
            Pageable pageable
    ) {
        List<Lesson> lessons = lessonRepository.findAll(specification, pageable).getContent();
        return lessons.stream()
                .map(lesson -> GlobalSearchResultResponse.builder()
                        .id(lesson.getId())
                        .type(SearchResultType.LESSON)
                        .title(lesson.getTitle())
                        .subtitle(lesson.getSection().getCourse().getTitle() + " / " + lesson.getSection().getTitle())
                        .description(snippet(lesson.getContent()))
                        .url("/lessons/" + lesson.getId())
                        .score(score(
                                query,
                                lesson.getTitle(),
                                lesson.getContent(),
                                lesson.getSection().getTitle(),
                                lesson.getSection().getCourse().getTitle()
                        ))
                        .build())
                .toList();
    }

    private String normalizeQuery(String query) {
        if (query == null || query.trim().length() < 2)
            throw new CustomBadRequestException("query must contain at least 2 characters");

        String normalizedQuery = query.trim().replaceAll("\\s+", " ");
        if (normalizedQuery.length() > MAX_QUERY_LENGTH)
            throw new CustomBadRequestException("query must be at most " + MAX_QUERY_LENGTH + " characters");

        return normalizedQuery;
    }

    private Set<SearchResultType> resolveTypes(Set<SearchResultType> types) {
        if (types == null || types.isEmpty())
            return EnumSet.allOf(SearchResultType.class);

        return EnumSet.copyOf(types);
    }

    private int normalizeSize(int size) {
        if (size <= 0)
            return DEFAULT_SIZE;

        return Math.min(size, MAX_SIZE);
    }

    private int score(String query, String title, String... searchableFields) {
        String normalizedQuery = query.toLowerCase(Locale.ROOT);
        String normalizedTitle = normalize(title);

        if (normalizedTitle.equals(normalizedQuery))
            return 100;

        if (normalizedTitle.startsWith(normalizedQuery))
            return 90;

        if (normalizedTitle.contains(normalizedQuery))
            return 75;

        for (String field : searchableFields) {
            if (normalize(field).contains(normalizedQuery))
                return 55;
        }

        return 40;
    }

    private String normalize(String value) {
        if (value == null)
            return "";

        return value.toLowerCase(Locale.ROOT);
    }

    private String snippet(String value) {
        if (value == null || value.isBlank())
            return null;

        String trimmedValue = value.trim().replaceAll("\\s+", " ");
        if (trimmedValue.length() <= DESCRIPTION_LENGTH)
            return trimmedValue;

        return trimmedValue.substring(0, DESCRIPTION_LENGTH - 3) + "...";
    }
}
