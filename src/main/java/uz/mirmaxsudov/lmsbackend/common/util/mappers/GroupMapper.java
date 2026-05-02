package uz.mirmaxsudov.lmsbackend.common.util.mappers;

import uz.mirmaxsudov.lmsbackend.model.entity.lms.Group;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.GroupStatus;
import uz.mirmaxsudov.lmsbackend.model.response.lms.GroupResponse;

public final class GroupMapper {

    private GroupMapper() {
        // Private constructor to prevent instantiation
    }

    public static GroupResponse toResponse(Group group) {
        if (group == null) {
            return null;
        }

        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getGroupName())
                .courseId(group.getCourse() == null ? null : group.getCourse().getId())
                .courseName(group.getCourse() == null ? null : group.getCourse().getTitle())
                .teacher(group.getTeacher() == null ? null : TeacherMapper.toGroupTeacherResponse(group.getTeacher().getUser(), group.getTeacher()))
                .capacity(group.getCapacity())
                .scheduleType(group.getScheduleType())
                .scheduleDays(group.getScheduleDays())
                .status(group.getStatus())
                .active(group.getStatus() == GroupStatus.ACTIVE)
                .currentStudents(group.getStudents() == null ? 0 : group.getStudents().size())
                .build();
    }
}
