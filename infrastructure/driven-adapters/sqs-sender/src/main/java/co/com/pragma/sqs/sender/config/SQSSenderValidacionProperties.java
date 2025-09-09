package co.com.pragma.sqs.sender.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adapter.sqs.validacion")
public record SQSSenderValidacionProperties(
     String region,
     String queueUrl,
     String endpoint){
}
