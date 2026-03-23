package uz.mirmaxsudov.lmsbackend.common.util.mappers;

import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.response.auth.AuthMe;

public final class AuthMeMapper {
    public static AuthMe toResponse(User user) {
        if (user == null)
            return AuthMe.builder().build();

        return AuthMe.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .middleName(user.getMiddleName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .status(user.getStatus())
                .birthDate(user.getBrithDate() == null ? null : user.getBrithDate().toString())
                .profileImageAttachmentId(
                        user.getProfileImageAttachment() == null ? null : user.getProfileImageAttachment().getId()
                )
                .profileImageUrl(
                        user.getProfileImageAttachment() == null ? null : user.getProfileImageAttachment().getUrl()
                )
                .profileBackgroundAttachmentId(
                        user.getProfileBackgroundAttachment() == null ? null : user.getProfileBackgroundAttachment().getId()
                )
                .profileBackgroundUrl(
                        user.getProfileBackgroundAttachment() == null ? null : user.getProfileBackgroundAttachment().getUrl()
                )
                .roles(user.getRoles())
                .build();
    }
}
