package uz.mirmaxsudov.lmsbackend.common.util.mappers;

import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.entity.content.Attachment;
import uz.mirmaxsudov.lmsbackend.model.response.auth.AuthMe;

public final class AuthMeMapper {
    public static AuthMe toResponse(User user) {
        if (user == null)
            return AuthMe.builder().build();

        return AuthMe.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .middleName(user.getMiddleName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .status(user.getStatus())
                .profileImageAttachmentId(
                        user.getProfileImage() == null ? null : user.getProfileImage().getId()
                )
                .profileImageUrl(
                        user.getProfileImage() == null ? null : user.getProfileImage().getUrl()
                )
                .profileBackgroundAttachmentId(
                        user.getProfileBackgroundImage() == null ? null : user.getProfileBackgroundImage().getId()
                )
                .profileBackgroundUrl(
                        user.getProfileBackgroundImage() == null ? null : user.getProfileBackgroundImage().getUrl()
                )
                .roles(RoleMapper.toResponses(user.getRoles()))
                .birthDate(user.getBirthDate())
                .build();
    }

    public static AuthMe toResponse(User user, Attachment profileImage, Attachment profileBackgroundImage) {
        if (user == null)
            return AuthMe.builder().build();

        return AuthMe.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .middleName(user.getMiddleName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .status(user.getStatus())
                .profileImageAttachmentId(profileImage == null ? null : profileImage.getId())
                .profileImageUrl(profileImage == null ? null : profileImage.getUrl())
                .profileBackgroundAttachmentId(profileBackgroundImage == null ? null : profileBackgroundImage.getId())
                .profileBackgroundUrl(profileBackgroundImage == null ? null : profileBackgroundImage.getUrl())
                .roles(RoleMapper.toResponses(user.getRoles()))
                .birthDate(user.getBirthDate())
                .build();
    }
}
