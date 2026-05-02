package uz.mirmaxsudov.lmsbackend.common.util.mappers;

import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Attendance;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.AttendanceNote;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.LessonSession;
import uz.mirmaxsudov.lmsbackend.model.entity.user.StudentProfile;
import uz.mirmaxsudov.lmsbackend.model.response.lms.AttendanceNoteResponse;

public final class AttendanceNoteMapper {

    private AttendanceNoteMapper() {
    }

    public static AttendanceNoteResponse toResponse(AttendanceNote attendanceNote) {
        if (attendanceNote == null) {
            return null;
        }

        Attendance attendance = attendanceNote.getAttendance();
        StudentProfile student = attendance == null ? null : attendance.getStudent();
        User user = student == null ? null : student.getUser();
        LessonSession lessonSession = attendance == null ? null : attendance.getLessonSession();

        return AttendanceNoteResponse.builder()
                .id(attendanceNote.getId())
                .attendanceId(attendance == null ? null : attendance.getId())
                .studentProfileId(student == null ? null : student.getId())
                .studentFullName(toFullName(user))
                .lessonSessionId(lessonSession == null ? null : lessonSession.getId())
                .groupId(lessonSession == null || lessonSession.getGroup() == null ? null : lessonSession.getGroup().getId())
                .groupName(lessonSession == null || lessonSession.getGroup() == null ? null : lessonSession.getGroup().getGroupName())
                .lessonId(lessonSession == null || lessonSession.getLesson() == null ? null : lessonSession.getLesson().getId())
                .lessonTitle(lessonSession == null || lessonSession.getLesson() == null ? null : lessonSession.getLesson().getTitle())
                .note(attendanceNote.getNote())
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
