package uz.mirmaxsudov.lmsbackend.common.util.mappers;

import uz.mirmaxsudov.lmsbackend.model.entity.auth.Role;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.response.user.user.UserPreview;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class UserMapper {

    private UserMapper() {
        // Private constructor to prevent instantiation
    }

    public static UserPreview toPreview(User user) {
        if (user == null) {
            return null;
        }

        return UserPreview.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .middleName(user.getMiddleName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .status(user.getStatus())
                .profileImageUrl(user.getProfileImage() == null ? null : user.getProfileImage().getUrl())
                .profileBackgroundUrl(user.getProfileBackgroundImage() == null ? null : user.getProfileBackgroundImage().getUrl())
                .roles(mapRoleNames(user.getRoles()))
                .build();
    }

    private static Set<String> mapRoleNames(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return Set.of();
        }

        return roles.stream()
                .map(Role::getName)
                .filter(name -> name != null && !name.isBlank())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
