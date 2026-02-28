package uz.mirmaxsudov.lmsbackend.model.response.auth;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String accessToken;
}