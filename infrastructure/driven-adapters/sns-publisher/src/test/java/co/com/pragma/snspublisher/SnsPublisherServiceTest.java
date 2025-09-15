package co.com.pragma.snspublisher;

import co.com.pragma.model.solicitud.common.ex.TechnicalException;
import co.com.pragma.snspublisher.config.SnsPublisherProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SnsPublisherServiceTest {
    @Mock
    private SnsAsyncClient snsAsyncClient;
    @Mock
    private SnsPublisherProperties properties;

    private SnsPublisherService snsPublisherService;

    record DummyMessage(String id, String contenido) {}

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        snsPublisherService = new SnsPublisherService<>(snsAsyncClient, properties, objectMapper);
    }

    @Test
    void publicar_exitoso() {
        DummyMessage mensaje = new DummyMessage("1", "hola");
        PublishResponse response = PublishResponse.builder().messageId("msg-123").build();

        when(snsAsyncClient.publish(any(PublishRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));
        when(properties.getTopicArn()).thenReturn("arn:aws:sns:us-east-1:123456789012:mi-topic");

        Mono<String> result = snsPublisherService.publicar(mensaje);

        StepVerifier.create(result).expectNext("msg-123").verifyComplete();

        verify(snsAsyncClient).publish(any(PublishRequest.class));
    }

    @Test
    void publicar_fallaSerializacion() {
        class Unserializable {
            public String getValue() {
                throw new RuntimeException("No se puede serializar");
            }
        }
        record DummyMessage(String id, Object valor) {}
        DummyMessage mensaje = new DummyMessage("1", new Unserializable());

        Mono<String> result = snsPublisherService.publicar(mensaje);

        StepVerifier.create(result)
                .expectErrorSatisfies(error -> assertThat(error)
                        .isInstanceOf(TechnicalException.class))
                .verify();

        verifyNoInteractions(snsAsyncClient); // nunca debería llamarse
    }

    @Test
    void publicar_fallaEnAws() {
        DummyMessage mensaje = new DummyMessage("1", "hola");

        CompletableFuture<PublishResponse> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("AWS SNS down"));

        when(snsAsyncClient.publish(any(PublishRequest.class))).thenReturn(failedFuture);
        when(properties.getTopicArn()).thenReturn("arn:aws:sns:us-east-1:123456789012:mi-topic");

        StepVerifier.create(snsPublisherService.publicar(mensaje))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().contains("AWS SNS down"))
                .verify();

        verify(snsAsyncClient).publish(any(PublishRequest.class));
    }
}
