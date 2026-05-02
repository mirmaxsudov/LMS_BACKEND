package uz.mirmaxsudov.lmsbackend.common.util.mappers;

import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Attendance;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.LessonSession;
import uz.mirmaxsudov.lmsbackend.model.entity.user.StudentProfile;
import uz.mirmaxsudov.lmsbackend.model.response.lms.AttendanceResponse;

public final class AttendanceMapper {

    private AttendanceMapper() {
    }

    public static AttendanceResponse toResponse(Attendance attendance) {
        if (attendance == null) {
            return null;
        }

        StudentProfile student = attendance.getStudent();
        User user = student == null ? null : student.getUser();
        LessonSession lessonSession = attendance.getLessonSession();

        return AttendanceResponse.builder()
                .id(attendance.getId())
                .studentId(student == null ? null : student.getStudentId())
                .studentProfileId(student == null ? null : student.getId())
                .studentFullName(toFullName(user))
                .lessonSessionId(lessonSession == null ? null : lessonSession.getId())
                .groupId(lessonSession == null || lessonSession.getGroup() == null ? null : lessonSession.getGroup().getId())
                .groupName(lessonSession == null || lessonSession.getGroup() == null ? null : lessonSession.getGroup().getGroupName())
                .lessonId(lessonSession == null || lessonSession.getLesson() == null ? null : lessonSession.getLesson().getId())
                .lessonTitle(lessonSession == null || lessonSession.getLesson() == null ? null : lessonSession.getLesson().getTitle())
                .lessonStartTime(lessonSession == null ? null : lessonSession.getStartTime())
                .lessonEndTime(lessonSession == null ? null : lessonSession.getEndTime())
                .status(attendance.getStatus())
                .build();
    }

    private static String toFullName(User user) {
        if (user == null) {
            return null;
        }

        return (normalize(user.getFirstName()) + " " + normalize(user.getLastName())).trim();
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
