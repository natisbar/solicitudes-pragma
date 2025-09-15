package co.com.pragma.snspublisher.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsAsyncClient;

@Configuration
public class SnsPublisherConfig {
    @Bean
    public SnsAsyncClient snsAsyncClient(SnsPublisherProperties snsPublisherProperties) {
        return SnsAsyncClient.builder()
                .region(Region.of(snsPublisherProperties.getRegion()))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
