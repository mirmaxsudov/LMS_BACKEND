package uz.mirmaxsudov.lmsbackend.common.util.mappers;

import uz.mirmaxsudov.lmsbackend.model.entity.lms.Group;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Schedule;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.GroupStatus;
import uz.mirmaxsudov.lmsbackend.model.response.lms.NextLessonResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.StudentGroupCourseResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.SyllabusProgressResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.TeacherGroupResponse;

import java.util.List;

public final class TeacherGroupMapper {

    private TeacherGroupMapper() {
    }

    public static TeacherGroupResponse toResponse(
            Group group,
            List<Schedule> schedules,
            SyllabusProgressResponse syllabusProgress,
            NextLessonResponse nextLesson
    ) {
        if (group == null) {
            return null;
        }

        return TeacherGroupResponse.builder()
                .id(group.getId())
                .groupName(group.getGroupName())
                .status(group.getStatus())
                .active(group.getStatus() == GroupStatus.ACTIVE)
                .capacity(group.getCapacity())
                .currentStudents(group.getStudents() == null ? 0 : group.getStudents().size())
                .scheduleType(group.getScheduleType())
                .scheduleDays(group.getScheduleDays())
                .schedules(schedules.stream().map(ScheduleMapper::toResponse).toList())
                .course(group.getCourse() == null ? null : StudentGroupCourseResponse.builder()
                        .id(group.getCourse().getId())
                        .title(group.getCourse().getTitle())
                        .description(group.getCourse().getDescription())
                        .level(group.getCourse().getLevel())
                        .durationInMinutes(group.getCourse().getDurationInMinutes())
                        .build())
                .syllabusProgress(syllabusProgress)
                .nextLesson(nextLesson)
                .build();
    }
}
