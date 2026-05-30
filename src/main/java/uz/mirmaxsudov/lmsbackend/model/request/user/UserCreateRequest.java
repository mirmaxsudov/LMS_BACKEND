package uz.mirmaxsudov.lmsbackend.model.request.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.annotations.validation.UniqueElements;
import uz.mirmaxsudov.lmsbackend.model.enums.Gender;
import uz.mirmaxsudov.lmsbackend.model.enums.auth.UserStatus;
import uz.mirmaxsudov.lmsbackend.model.request.auth.RoleRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserCreateRequest {
    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must be at most 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must be at most 100 characters")
    private String lastName;

    @Size(max = 100, message = "Middle name must be at most 100 characters")
    private String middleName;

    @NotNull(message = "Gender is required")
    private Gender gender;

    private LocalDateTime birthDate;

    @NotBlank(message = "Phone number is required")
    @Size(max = 30, message = "Phone number must be at most 30 characters")
    @Pattern(regexp = "^[+0-9()\\-\\s]{7,30}$", message = "Phone number format is invalid")
    private String phoneNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must be at most 255 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    private String password;

    @NotNull(message = "Status is required")
    private UserStatus status = UserStatus.ACTIVE;

    @Valid
    @UniqueElements(property = "id", message = "Roles contain duplicate ids")
    private List<RoleRequest> roles = new ArrayList<>();
}
