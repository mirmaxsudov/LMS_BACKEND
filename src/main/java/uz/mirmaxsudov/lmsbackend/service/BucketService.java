package uz.mirmaxsudov.lmsbackend.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.mirmaxsudov.lmsbackend.config.rateLimiterConfig.RateLimitProperties;
import uz.mirmaxsudov.lmsbackend.util.RateLimitStorage;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class BucketService {
    private final RateLimitStorage rateLimitStorage;

    public Bucket getOrCreateBucket(String key, RateLimitProperties.LimitConfig config) {
        return rateLimitStorage.getOrCreateBucket(key, () -> buildBucket(config));
    }

    private Bucket buildBucket(RateLimitProperties.LimitConfig config) {
        Bandwidth limit = Bandwidth.classic(config.getCapacity(),
                Refill.intervally(config.getRefill(), Duration.ofSeconds(config.getDuration())));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
