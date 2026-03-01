package uz.mirmaxsudov.lmsbackend.service.impl.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomBadRequestException;
import uz.mirmaxsudov.lmsbackend.exceptions.CustomNotFoundException;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.request.auth.LoginRequest;
import uz.mirmaxsudov.lmsbackend.model.response.ApiResponse;
import uz.mirmaxsudov.lmsbackend.model.response.auth.AuthMe;
import uz.mirmaxsudov.lmsbackend.model.response.auth.JwtResponse;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetails;
import uz.mirmaxsudov.lmsbackend.security.service.JwtService;
import uz.mirmaxsudov.lmsbackend.service.base.UserService;
import uz.mirmaxsudov.lmsbackend.service.base.auth.AuthService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtService jwtService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<ApiResponse<JwtResponse>> login(LoginRequest request, HttpServletRequest servletRequest) {
        var user = userService.getUserByEmail(request.getUsername())
                .orElseThrow(() -> new CustomNotFoundException("User not found with email: " + request.getUsername()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new CustomBadRequestException("Invalid password");

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        JwtResponse jwtResponse = JwtResponse.builder()
                .accessToken(accessToken)
                .build();

        return ResponseEntity.ok(ApiResponse.<JwtResponse>builder()
                .message("Login successful")
                .data(jwtResponse)
                .success(true)
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Logout successful")
                .success(true)
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<AuthMe>> getMe(CustomUserDetails details) {
        User user = details.user();
        return ResponseEntity.ok(ApiResponse.<AuthMe>builder()
                .message("Get me successful")
                .success(true)
                .data(AuthMe.builder()
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .middleName(user.getMiddleName())
                        .email(user.getEmail())
                        .phoneNumber(user.getPhoneNumber())
                        .email(user.getEmail())
                        .gender(user.getGender())
                        .roles(user.getRoles())
                        .build())
                .build());
    }
}
