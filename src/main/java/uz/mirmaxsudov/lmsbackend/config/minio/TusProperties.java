package uz.mirmaxsudov.lmsbackend.config.minio;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "tus")
public class TusProperties {
    private long maxUploadSizeBytes = 10L * 1024 * 1024 * 1024;
    private boolean chunkCleanupOnComplete = true;
}
