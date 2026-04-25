package uz.mirmaxsudov.lmsbackend.common.util.mappers;

import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.response.course.GroupTeacherResponse;

public final class TeacherMapper {
    public static GroupTeacherResponse toGroupTeacherResponse(User user) {
        if (user == null)
            return GroupTeacherResponse.builder().build();

        return GroupTeacherResponse.builder()
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