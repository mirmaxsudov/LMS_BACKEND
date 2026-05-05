package uz.mirmaxsudov.lmsbackend.common.util.mappers;

import uz.mirmaxsudov.lmsbackend.model.entity.lms.CourseSection;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Lesson;
import uz.mirmaxsudov.lmsbackend.model.response.lms.LessonResponse;

public final class LessonMapper {
    public static LessonResponse toResponse(Lesson lesson) {
        if (lesson == null)
            return null;

        CourseSection section = lesson.getSection();

        return LessonResponse.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .content(lesson.getContent())
                .durationInMinutes(lesson.getDurationInMinutes())
                .sectionId(section != null ? section.getId() : null)
                .sectionTitle(section != null ? section.getTitle() : null)
                .build();
    }
}
