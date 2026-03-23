package uz.mirmaxsudov.lmsbackend.config.rateLimiterConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    int capacity() default 10;

    int refill() default 10;

    int duration() default 60;
}
