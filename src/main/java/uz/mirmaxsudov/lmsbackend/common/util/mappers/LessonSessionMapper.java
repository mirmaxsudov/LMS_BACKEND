package uz.mirmaxsudov.lmsbackend.common.util.mappers;

import uz.mirmaxsudov.lmsbackend.model.entity.lms.LessonSession;
import uz.mirmaxsudov.lmsbackend.model.response.lms.LessonSessionResponse;

public final class LessonSessionMapper {

    private LessonSessionMapper() {
    }

    public static LessonSessionResponse toResponse(LessonSession lessonSession) {
        if (lessonSession == null) {
            return null;
        }

        return LessonSessionResponse.builder()
                .id(lessonSession.getId())
                .groupId(lessonSession.getGroup() == null ? null : lessonSession.getGroup().getId())
                .groupName(lessonSession.getGroup() == null ? null : lessonSession.getGroup().getGroupName())
                .lessonId(lessonSession.getLesson() == null ? null : lessonSession.getLesson().getId())
                .lessonTitle(lessonSession.getLesson() == null ? null : lessonSession.getLesson().getTitle())
                .startTime(lessonSession.getStartTime())
                .endTime(lessonSession.getEndTime())
                .status(lessonSession.getStatus())
                .build();
    }
}
