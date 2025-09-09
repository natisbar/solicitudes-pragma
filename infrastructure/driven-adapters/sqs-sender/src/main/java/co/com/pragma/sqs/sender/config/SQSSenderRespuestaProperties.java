package co.com.pragma.sqs.sender.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adapter.sqs.respuesta")
public record SQSSenderRespuestaProperties(
     String region,
     String queueUrl,
     String endpoint){
}
