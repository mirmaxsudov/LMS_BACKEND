package uz.mirmaxsudov.lmsbackend.common.util.mappers;

import uz.mirmaxsudov.lmsbackend.model.entity.user.StudentProfile;
import uz.mirmaxsudov.lmsbackend.model.response.user.user.StudentProfileResponse;

public final class StudentMapper {

    private StudentMapper() {
        // Private constructor to prevent instantiation
    }

    public static StudentProfileResponse toResponse(StudentProfile profile) {
        if (profile == null) {
            return null;
        }

        return StudentProfileResponse.builder()
                .baseData(AuthMeMapper.toResponse(profile.getUser()))
                .studentId(profile.getStudentId())
                .status(profile.getStatus())
                .build();
    }
}
