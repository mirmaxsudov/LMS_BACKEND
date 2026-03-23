package uz.mirmaxsudov.lmsbackend.config.rateLimiterConfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "rate-limit")
public class RateLimitProperties {

    /**
     * Default rate limit for all requests if no other rule matches.
     */
    private LimitConfig defaultLimit = new LimitConfig(100, 100, 60);

    /**
     * Endpoint-specific limits. Key is an ant-style path pattern (e.g., "/api/auth/**").
     */
    private Map<String, LimitConfig> endpointLimits = new HashMap<>();

    /**
     * User-specific limits based on userId.
     */
    private Map<String, LimitConfig> userLimits = new HashMap<>();

    /**
     * IP specific limits.
     */
    private Map<String, LimitConfig> ipLimits = new HashMap<>();

    @Data
    public static class LimitConfig {
        /**
         * Maximum number of tokens in the bucket.
         */
        private int capacity;

        /**
         * Number of tokens to refill.
         */
        private int refill;

        /**
         * Duration for refill in seconds.
         */
        private int duration;

        public LimitConfig() {}

        public LimitConfig(int capacity, int refill, int duration) {
            this.capacity = capacity;
            this.refill = refill;
            this.duration = duration;
        }
    }
}
