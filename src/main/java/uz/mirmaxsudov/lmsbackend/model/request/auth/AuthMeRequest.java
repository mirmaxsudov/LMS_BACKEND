package uz.mirmaxsudov.lmsbackend.model.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.enums.Gender;

import java.time.LocalDateTime;

@Getter
@Setter
public class AuthMeRequest {
    @Size(max = 100, message = "First name must be at most 100 characters")
    private String firstName;
    @Size(max = 100, message = "Last name must be at most 100 characters")
    private String lastName;
    @Size(max = 100, message = "Middle name must be at most 100 characters")
    private String middleName;
    private Gender gender;
    @Size(max = 30, message = "Phone number must be at most 30 characters")
    private String phoneNumber;
    private LocalDateTime brithDate;
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must be at most 255 characters")
    private String email;
}
