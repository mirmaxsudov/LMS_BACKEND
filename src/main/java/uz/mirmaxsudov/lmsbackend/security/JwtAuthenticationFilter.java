package uz.mirmaxsudov.lmsbackend.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import uz.mirmaxsudov.lmsbackend.exceptions.InvalidTokenException;
import uz.mirmaxsudov.lmsbackend.security.service.CustomUserDetailsService;
import uz.mirmaxsudov.lmsbackend.security.service.JwtService;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || authHeader.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String[] authorizationParts = authHeader.trim().split("\\s+");
            if (authorizationParts.length == 0 || !"Bearer".equalsIgnoreCase(authorizationParts[0])) {
                filterChain.doFilter(request, response);
                return;
            }

            if (authorizationParts.length != 2)
                throw new InvalidTokenException("Authorization header must be 'Bearer <token>'");

            final String jwt = authorizationParts[1];

            if (jwt.isEmpty())
                throw new InvalidTokenException("JWT token is missing after Bearer prefix");

            final String email = jwtService.extractUsername(jwt);

            if (email == null || email.isBlank())
                throw new InvalidTokenException("JWT token subject is missing");

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    var authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } else {
                    throw new InvalidTokenException("JWT token is invalid or expired");
                }
            }
            filterChain.doFilter(request, response);
        } catch (BadCredentialsException | InvalidTokenException | UsernameNotFoundException | SignatureException | ExpiredJwtException |
                 MalformedJwtException e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}
