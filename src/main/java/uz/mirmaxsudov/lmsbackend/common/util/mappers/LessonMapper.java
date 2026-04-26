package uz.mirmaxsudov.lmsbackend.common.util.mappers;

import uz.mirmaxsudov.lmsbackend.model.entity.lms.Lesson;
import uz.mirmaxsudov.lmsbackend.model.response.lms.LessonResponse;

public final class LessonMapper {

    private LessonMapper() {
        // Private constructor to prevent instantiation
    }

    public static LessonResponse toResponse(Lesson lesson) {
        if (lesson == null) {
            return null;
        }

        return LessonResponse.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .content(lesson.getContent())
                .durationInMinutes(lesson.getDurationInMinutes())
                .sectionId(lesson.getSection() != null ? lesson.getSection().getId() : null)
                .build();
    }
}
