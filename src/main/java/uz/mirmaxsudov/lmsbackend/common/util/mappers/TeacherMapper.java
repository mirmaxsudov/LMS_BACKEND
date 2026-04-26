package uz.mirmaxsudov.lmsbackend.common.util.mappers;

import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.entity.user.TeacherProfile;
import uz.mirmaxsudov.lmsbackend.model.response.course.GroupTeacherResponse;
import uz.mirmaxsudov.lmsbackend.model.response.user.user.TeacherProfileResponse;

public final class TeacherMapper {

    private TeacherMapper() {
        // Private constructor to prevent instantiation
    }

    public static TeacherProfileResponse toResponse(TeacherProfile profile) {
        if (profile == null) {
            return null;
        }

        return TeacherProfileResponse.builder()
                .teacherId(profile.getId())
                .position(profile.getPosition())
                .user(AuthMeMapper.toResponse(profile.getUser()))
                .build();
    }
    public static GroupTeacherResponse toGroupTeacherResponse(User user, TeacherProfile teacherProfile) {
        if (user == null)
            return GroupTeacherResponse.builder().build();

        return GroupTeacherResponse.builder()
                .userId(user.getId())
                .teacherId(teacherProfile.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .middleName(user.getMiddleName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .status(user.getStatus())
                .profileImageUrl(
                        user.getProfileImage() == null ? null : user.getProfileImage().getUrl()
                )
                .profileBackgroundUrl(
                        user.getProfileBackgroundImage() == null ? null : user.getProfileBackgroundImage().getUrl()
                )
                .build();
    }

}