package co.com.pragma.snspublisher;

import co.com.pragma.model.solicitud.gateways.PublicacionGateway;
import co.com.pragma.snspublisher.config.SnsPublisherProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Service
public class SnsPublisherService<T>  implements PublicacionGateway<T> {

    private final SnsAsyncClient snsAsyncClient;
    private final SnsPublisherProperties snsPublisherProperties;
    private final ObjectMapper objectMapper;

    public SnsPublisherService(SnsAsyncClient snsAsyncClient, SnsPublisherProperties snsPublisherProperties, ObjectMapper objectMapper) {
        this.snsAsyncClient = snsAsyncClient;
        this.snsPublisherProperties = snsPublisherProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> publicar(T mensaje) {
        PublishRequest request = PublishRequest.builder()
                .topicArn(snsPublisherProperties.getTopicArn())
                .message(convertirAString(mensaje))
                .build();

        return Mono.fromFuture(() -> snsAsyncClient.publish(request))
                .doOnSuccess(response -> {
                    System.out.println("Message published with ID: " + response.messageId());
                }).then();
    }

    private String convertirAString(T objetoMensaje){
        try {
            return objectMapper.writeValueAsString(objetoMensaje);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
