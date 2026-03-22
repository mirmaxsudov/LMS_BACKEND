package uz.mirmaxsudov.lmsbackend.util;

import io.github.bucket4j.Bucket;

import java.util.function.Supplier;

public interface RateLimitStorage {
    /**
     * Get or create a bucket for the given key.
     *
     * @param key            unique key for the bucket
     * @param bucketSupplier supplier to create a new bucket if it doesn't exist
     * @return the existing or newly created bucket
     */
    Bucket getOrCreateBucket(String key, Supplier<Bucket> bucketSupplier);
}
