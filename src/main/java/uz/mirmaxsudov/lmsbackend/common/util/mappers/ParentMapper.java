package uz.mirmaxsudov.lmsbackend.common.util.mappers;

import uz.mirmaxsudov.lmsbackend.model.entity.user.ParentProfile;
import uz.mirmaxsudov.lmsbackend.model.response.user.user.ParentProfileResponse;

public final class ParentMapper {

    private ParentMapper() {
        // Private constructor to prevent instantiation
    }

    public static ParentProfileResponse toResponse(ParentProfile profile) {
        if (profile == null) {
            return null;
        }

        return ParentProfileResponse.builder()
                .baseData(AuthMeMapper.toResponse(profile.getUser()))
                .studentsCount(profile.getStudents() == null ? 0 : profile.getStudents().size())
                .build();
    }
}
