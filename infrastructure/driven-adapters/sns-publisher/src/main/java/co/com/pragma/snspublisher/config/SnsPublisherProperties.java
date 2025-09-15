package co.com.pragma.snspublisher.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adapter.sns")
@Getter
@Setter
public class SnsPublisherProperties {
    String region;
    String topicArn;
}
