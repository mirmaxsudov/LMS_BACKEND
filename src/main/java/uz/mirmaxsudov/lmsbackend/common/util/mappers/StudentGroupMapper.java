package uz.mirmaxsudov.lmsbackend.common.util.mappers;

import lombok.NoArgsConstructor;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Group;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Schedule;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.GroupStatus;
import uz.mirmaxsudov.lmsbackend.model.response.lms.GroupClassmateResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.NextLessonResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.StudentGroupCourseResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.StudentGroupResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.SyllabusProgressResponse;

import java.util.List;

@NoArgsConstructor
public final class StudentGroupMapper {

    public static StudentGroupResponse toResponse(
            Group group,
            List<Schedule> schedules,
            SyllabusProgressResponse syllabusProgress,
            NextLessonResponse nextLesson,
            int classmatesCount,
            List<GroupClassmateResponse> classmates
    ) {
        if (group == null) {
            return null;
        }

        return StudentGroupResponse.builder()
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
                .teacher(group.getTeacher() == null ? null : TeacherMapper.toGroupTeacherResponse(group.getTeacher().getUser(), group.getTeacher()))
                .syllabusProgress(syllabusProgress)
                .nextLesson(nextLesson)
                .classmatesCount(classmatesCount)
                .classmates(classmates)
                .build();
    }
}
