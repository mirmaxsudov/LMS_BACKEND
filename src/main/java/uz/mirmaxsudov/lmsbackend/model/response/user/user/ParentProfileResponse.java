package uz.mirmaxsudov.lmsbackend.model.response.user.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.response.auth.AuthMe;

@Getter
@Setter
@Builder
public class ParentProfileResponse {
    private AuthMe baseData;
    private int studentsCount;
}
