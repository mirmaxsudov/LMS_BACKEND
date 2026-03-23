package uz.mirmaxsudov.lmsbackend.config.rateLimiterConfig;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import uz.mirmaxsudov.lmsbackend.service.BucketService;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final BucketService bucketService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {
            RateLimit rateLimit = handlerMethod.getMethodAnnotation(RateLimit.class);
            if (rateLimit != null) {
                String key = "annotation:" + handlerMethod.getMethod().getName() + ":" + request.getRemoteAddr();
                RateLimitProperties.LimitConfig config = new RateLimitProperties.LimitConfig(
                        rateLimit.capacity(), rateLimit.refill(), rateLimit.duration());
                
                Bucket bucket = bucketService.getOrCreateBucket(key, config);
                ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

                if (probe.isConsumed()) {
                    response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
                    return true;
                } else {
                    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                    response.addHeader("X-Rate-Limit-Retry-After", String.valueOf(probe.getNanosToWaitForRefill() / 1_000_000_000));
                    response.getWriter().write("Too many requests on this endpoint.");
                    return false;
                }
            }
        }
        return true;
    }
}
