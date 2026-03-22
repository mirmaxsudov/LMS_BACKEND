package uz.mirmaxsudov.lmsbackend.config.annatations;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;
import uz.mirmaxsudov.lmsbackend.annotations.OpenAuth;

import java.util.*;

@Configuration
@RequiredArgsConstructor
public class OpenAuthWhiteListConfig {
    @Bean
    public List<RequestMatcher> openAuthWhiteList(
            RequestMappingHandlerMapping handlerMapping
    ) {

        List<RequestMatcher> matchers = new ArrayList<>();

        PathPatternRequestMatcher.Builder builder =
                PathPatternRequestMatcher.withDefaults();

        Map<RequestMappingInfo, HandlerMethod> mp =
                handlerMapping.getHandlerMethods();

        for (var entry : mp.entrySet()) {

            RequestMappingInfo info = entry.getKey();
            HandlerMethod handler = entry.getValue();

            boolean isOpen =
                    handler.hasMethodAnnotation(OpenAuth.class) ||
                            handler.getBeanType().isAnnotationPresent(OpenAuth.class);

            if (!isOpen) continue;

            Set<RequestMethod> methods =
                    info.getMethodsCondition().getMethods();

            Set<PathPattern> patterns =
                    info.getPathPatternsCondition().getPatterns();

            for (PathPattern pp : patterns) {

                String pattern = pp.getPatternString();

                if (methods.isEmpty()) {

                    matchers.add(builder.matcher(pattern));

                } else {

                    for (RequestMethod method : methods) {

                        matchers.add(
                                builder.matcher(
                                        HttpMethod.valueOf(method.name()),
                                        pattern
                                )
                        );
                    }
                }
            }
        }

        return List.copyOf(matchers);
    }
}