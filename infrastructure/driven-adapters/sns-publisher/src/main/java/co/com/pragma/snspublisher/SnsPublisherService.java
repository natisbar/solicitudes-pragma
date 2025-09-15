package co.com.pragma.snspublisher;

import co.com.pragma.model.solicitud.common.ex.TechnicalException;
import co.com.pragma.model.solicitud.gateways.PublicacionGateway;
import co.com.pragma.snspublisher.config.SnsPublisherProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

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
    public Mono<String> publicar(T mensaje) {
        return Mono.fromCallable(() -> convertirAString(mensaje))
                .map(json -> PublishRequest.builder()
                        .topicArn(snsPublisherProperties.getTopicArn())
                        .message(json)
                        .build())
                .flatMap(request -> Mono.fromFuture(() -> snsAsyncClient.publish(request)))
                .map(PublishResponse::messageId);
    }

    private String convertirAString(T objetoMensaje){
        try {
            return objectMapper.writeValueAsString(objetoMensaje);
        } catch (JsonProcessingException e) {
            throw new TechnicalException(e.getMessage());
        }
    }
}
