package uz.mirmaxsudov.lmsbackend.common.util.mappers;

import uz.mirmaxsudov.lmsbackend.model.entity.lms.CourseSection;
import uz.mirmaxsudov.lmsbackend.model.response.lms.CourseSectionResponse;

import java.util.Collections;

public final class CourseSectionMapper {

    private CourseSectionMapper() {
        // Private constructor to prevent instantiation
    }

    public static CourseSectionResponse toResponse(CourseSection section) {
        if (section == null) {
            return null;
        }

        CourseSectionResponse response = CourseSectionResponse.builder()
                .id(section.getId())
                .title(section.getTitle())
                .orderIndex(section.getOrderIndex())
                .courseId(section.getCourse() != null ? section.getCourse().getId() : null)
                .build();

        if (section.getLessons() != null && !section.getLessons().isEmpty()) {
            response.setLessons(section.getLessons().stream()
                    .filter(lesson -> !lesson.isDeleted())
                    .map(LessonMapper::toResponse)
                    .toList());
        } else if (section.getLessons() != null) {
            response.setLessons(Collections.emptyList());
        }

        return response;
    }
}
