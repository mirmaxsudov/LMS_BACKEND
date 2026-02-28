package uz.mirmaxsudov.lmsbackend.model.request.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @NotNull(message = "Username is required")
    private String username;
    @NotNull(message = "Password is required")
    private String password;
}
