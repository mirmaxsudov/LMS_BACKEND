package uz.mirmaxsudov.lmsbackend.common.util.lms;

import uz.mirmaxsudov.lmsbackend.common.util.mappers.AuthMeMapper;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Course;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Enrollment;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.LmsGroup;
import uz.mirmaxsudov.lmsbackend.model.enums.lms.EnrollmentStatus;
import uz.mirmaxsudov.lmsbackend.model.response.lms.CourseResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.EnrollmentResponse;
import uz.mirmaxsudov.lmsbackend.model.response.lms.GroupResponse;

public final class LmsResponseMapper {
    private LmsResponseMapper() {
    }

    public static CourseResponse toCourseResponse(Course entity) {
        return CourseResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .description(entity.getDescription())
                .active(entity.isActive())
                .teacherId(entity.getTeacher() == null ? null : entity.getTeacher().getId())
                .teacher(AuthMeMapper.toResponse(entity.getTeacher()))
                .build();
    }

    public static GroupResponse toGroupResponse(LmsGroup entity) {
        int activeStudents = entity.getEnrollments() == null ? 0 : (int) entity.getEnrollments().stream()
                .filter(enrollment -> EnrollmentStatus.ACTIVE.equals(enrollment.getStatus()))
                .count();

        return GroupResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .courseId(entity.getCourse() == null ? null : entity.getCourse().getId())
                .courseName(entity.getCourse() == null ? null : entity.getCourse().getName())
                .teacherId(entity.getTeacher() == null ? null : entity.getTeacher().getId())
                .teacher(AuthMeMapper.toResponse(entity.getTeacher()))
                .capacity(entity.getCapacity())
                .active(entity.isActive())
                .currentStudents(activeStudents)
                .build();
    }

    public static EnrollmentResponse toEnrollmentResponse(Enrollment entity) {
        return EnrollmentResponse.builder()
                .id(entity.getId())
                .groupId(entity.getGroup() == null ? null : entity.getGroup().getId())
                .groupName(entity.getGroup() == null ? null : entity.getGroup().getName())
                .studentProfileId(entity.getStudentProfile() == null ? null : entity.getStudentProfile().getId())
                .studentId(entity.getStudentProfile() == null ? null : entity.getStudentProfile().getStudentId())
                .student(AuthMeMapper.toResponse(entity.getStudentProfile() == null ? null : entity.getStudentProfile().getUser()))
                .status(entity.getStatus())
                .enrolledAt(entity.getEnrolledAt())
                .build();
    }
}

