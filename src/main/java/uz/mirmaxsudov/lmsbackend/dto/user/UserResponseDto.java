package uz.mirmaxsudov.lmsbackend.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class UserResponseDto {
    private UUID id;
    private String fullName;
    private String phoneNumber;
    private String email;
}
