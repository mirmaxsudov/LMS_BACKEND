package uz.mirmaxsudov.lmsbackend.filter;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import uz.mirmaxsudov.lmsbackend.config.rateLimiterConfig.RateLimitProperties;
import uz.mirmaxsudov.lmsbackend.service.BucketService;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final BucketService bucketService;
    private final RateLimitProperties properties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        RateLimitProperties.LimitConfig config = properties.getDefaultLimit();
        String key = "global";

        for (Map.Entry<String, RateLimitProperties.LimitConfig> entry : properties.getEndpointLimits().entrySet()) {
            String pattern = entry.getKey().replaceAll("\\[\\s*|\\s*\\]", "");
            if (pathMatcher.match(pattern, uri)) {
                config = entry.getValue();
                key = "endpoint:" + pattern;
                break;
            }
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails userDetails) {
            String userId = userDetails.getUsername();
            String userKey = "[ " + userId + " ]";
            if (properties.getUserLimits().containsKey(userKey))
                config = properties.getUserLimits().get(userKey);

            key += ":user:" + userId;
        } else {
            String ip = request.getRemoteAddr();
            String ipKey = "[ " + ip + " ]";
            if (properties.getIpLimits().containsKey(ipKey))
                config = properties.getIpLimits().get(ipKey);

            key += ":ip:" + ip;
        }

        Bucket bucket = bucketService.getOrCreateBucket(key, config);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.addHeader("X-Rate-Limit-Retry-After", String.valueOf(probe.getNanosToWaitForRefill() / 1_000_000_000));
            response.getWriter().write("Too many requests. Please try again later.");
        }
    }
}
