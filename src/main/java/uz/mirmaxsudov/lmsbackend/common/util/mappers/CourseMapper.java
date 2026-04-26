package uz.mirmaxsudov.lmsbackend.common.util.mappers;

import uz.mirmaxsudov.lmsbackend.model.entity.lms.Course;
import uz.mirmaxsudov.lmsbackend.model.response.lms.CourseResponse;

import java.util.Collections;

public final class CourseMapper {
    public static CourseResponse toResponse(Course course) {
        if (course == null)
            return null;

        CourseResponse response = CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .level(course.getLevel())
                .durationInMinutes(course.getDurationInMinutes())
                .build();

        if (course.getSections() != null && !course.getSections().isEmpty()) {
            response.setSections(course.getSections().stream()
                    .filter(section -> !section.isDeleted())
                    .map(CourseSectionMapper::toResponse)
                    .toList());
        } else if (course.getSections() != null)
            response.setSections(Collections.emptyList());

        return response;
    }
}
