package uz.mirmaxsudov.lmsbackend.common.util.mappers;

import uz.mirmaxsudov.lmsbackend.model.entity.lms.Course;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.CourseSection;
import uz.mirmaxsudov.lmsbackend.model.response.lms.CourseSectionResponse;

import java.util.Collections;

public final class CourseSectionMapper {
    public static CourseSectionResponse toResponse(CourseSection section) {
        if (section == null)
            return null;


        Course course = section.getCourse();

        CourseSectionResponse response = CourseSectionResponse.builder()
                .id(section.getId())
                .title(section.getTitle())
                .orderIndex(section.getOrderIndex())
                .courseId(course != null ? course.getId() : null)
                .courseTitle(course != null ? course.getTitle() : null)
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
