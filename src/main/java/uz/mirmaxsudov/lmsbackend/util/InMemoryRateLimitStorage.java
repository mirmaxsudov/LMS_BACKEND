package uz.mirmaxsudov.lmsbackend.util;

import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Component
public class InMemoryRateLimitStorage implements RateLimitStorage {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public Bucket getOrCreateBucket(String key, Supplier<Bucket> bucketSupplier) {
        return buckets.computeIfAbsent(key, k -> bucketSupplier.get());
    }
}
