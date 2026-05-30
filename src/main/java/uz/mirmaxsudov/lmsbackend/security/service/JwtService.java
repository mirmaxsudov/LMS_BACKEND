package uz.mirmaxsudov.lmsbackend.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.Permission;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.Role;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:jwt.properties")
public class JwtService {
    @Value("${jwt.secret}")
    private String key;
    @Value("${jwt.access.expiration-ms}")
    private Long accessTokenExpiration;
    @Value("${jwt.refresh.expiration-ms}")
    private Long refreshTokenExpiration;

    public String generateAccessToken(User user) {
        return buildToken(new HashMap<>(), user, accessTokenExpiration);
    }

    public String generateRefreshToken(User user) {
        return buildToken(new HashMap<>(), user, refreshTokenExpiration);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        String normalizedToken = token == null ? "" : token.trim();
        if (normalizedToken.isEmpty()) {
            throw new MalformedJwtException("JWT token is empty");
        }
        if (normalizedToken.chars().anyMatch(Character::isWhitespace)) {
            throw new MalformedJwtException("JWT token must not contain whitespace");
        }
        return Jwts
                .parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(normalizedToken)
                .getPayload();
    }


    private String buildToken(Map<String, Object> extraClaims, User user, long expiration) {
        var roles = extractRoles(user);
        var permissions = extractPermissions(user);

        extraClaims.put("roles", roles);
        extraClaims.put("permissions", permissions);

        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey(), Jwts.SIG.HS512)
                .compact();
    }

    private Set<String> extractRoles(User user) {
        return user.getRoles().stream()
                .filter(role -> role != null && !role.isDeleted())
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    private Set<String> extractPermissions(User user) {
        return user.getRoles().stream()
                .filter(role -> role != null && !role.isDeleted())
                .flatMap(role -> role.getPermissions().stream())
                .filter(permission -> permission != null && !permission.isDeleted())
                .map(Permission::getCode)
                .collect(Collectors.toSet());
    }

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
    }
}
