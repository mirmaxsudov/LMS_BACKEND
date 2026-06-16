package uz.mirmaxsudov.lmsbackend.model.response.lms;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class GroupClassmateResponse {
    private UUID studentId;
    private String firstName;
    private String lastName;
    private String profileImageUrl;
}
