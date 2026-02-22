package uz.mirmaxsudov.lmsbackend.config.hibernate;

import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uz.mirmaxsudov.lmsbackend.listener.SoftDeleteEventListener;

@Configuration
public class HibernateConfig {
    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(SoftDeleteEventListener listener) {
        return hibernateProperties -> hibernateProperties.put(
                "hibernate.ejb.event.delete", listener
        );
    }
}