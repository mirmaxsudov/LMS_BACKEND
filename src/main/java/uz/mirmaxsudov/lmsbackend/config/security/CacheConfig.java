package uz.mirmaxsudov.lmsbackend.config.security;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

@Configuration
public class CacheConfig {
    public static final String USER_ROLES = "userRoles";
    public static final String USER_PERMISSIONS = "userPermissions";
    public static final String USER_AUTHORITIES = "userAuthorities";
    public static final String AUTH_ME = "authMe";

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        cacheManager.setCaches(List.of(
                new CaffeineCache(USER_ROLES, Caffeine.newBuilder()
                        .maximumSize(10_000)
                        .expireAfterWrite(Duration.ofMinutes(5))
                        .recordStats()
                        .build()),

                new CaffeineCache(USER_PERMISSIONS, Caffeine.newBuilder()
                        .maximumSize(10_000)
                        .expireAfterWrite(Duration.ofMinutes(5))
                        .recordStats()
                        .build()),

                new CaffeineCache(USER_AUTHORITIES, Caffeine.newBuilder()
                        .maximumSize(10_000)
                        .expireAfterWrite(Duration.ofMinutes(3))
                        .recordStats()
                        .build()),

                new CaffeineCache(AUTH_ME, Caffeine.newBuilder()
                        .maximumSize(10_000)
                        .expireAfterWrite(Duration.ofMinutes(3))
                        .recordStats()
                        .build())
        ));

        return cacheManager;
    }
}
