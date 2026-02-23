package uz.mirmaxsudov.lmsbackend.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

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

    public String generateAccessToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, accessTokenExpiration);
    }

    public String generateAccessToken(UserDetails details, Long sessionId) {
        return buildToken(new HashMap<>(), details, accessTokenExpiration, sessionId);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshTokenExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails, Long sessionId) {
        return buildToken(new HashMap<>(), userDetails, refreshTokenExpiration, sessionId);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        return (extractUsername(token).equals(userDetails.getUsername()) && isTokenExpired(token));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenExpired(String token) {
        return !extractAllClaims(token).getExpiration().before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails details, Long expiration, Long sessionId) {
        return Jwts
                .builder()
                .id(sessionId.toString())
                .claims(extraClaims)
                .subject(details.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey(), Jwts.SIG.HS512).compact();
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey(), Jwts.SIG.HS512).compact();
    }


    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
    }
}