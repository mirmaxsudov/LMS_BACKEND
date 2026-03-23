package uz.mirmaxsudov.lmsbackend.dto.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMeRequestDto {
    @Size(max = 200, message = "fullName must not exceed 200 characters")
    private String fullName;

    @Pattern(
            regexp = "^\\+?[0-9\\-() ]{7,20}$",
            message = "phoneNumber format is invalid"
    )
    private String phoneNumber;
}
