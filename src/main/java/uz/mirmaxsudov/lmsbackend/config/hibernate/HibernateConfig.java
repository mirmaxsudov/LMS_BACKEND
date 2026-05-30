package uz.mirmaxsudov.lmsbackend.config.hibernate;

import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uz.mirmaxsudov.lmsbackend.listener.SoftDeleteEventListener;

@Configuration
public class HibernateConfig {

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(
            SoftDeleteEventListener listener
    ) {
        return properties ->
                properties.put("hibernate.ejb.event.delete", listener);
    }
}