package co.com.pragma.sqs.sender;

import co.com.pragma.sqs.sender.config.SQSSenderRespuestaProperties;
import co.com.pragma.sqs.sender.config.SQSSenderValidacionProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class SQSSenderTest {

    private SQSSender<DummyMensaje> sender;

    @Mock
    private SQSSenderValidacionProperties propertiesValidacion;

    @Mock
    private SQSSenderRespuestaProperties propertiesRespuesta;

    @Mock
    private SqsAsyncClient clientValidacion;

    @Mock
    private SqsAsyncClient clientRespuesta;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String queueUrlValidacion = "https://sqs.validacion.url";
    private final String queueUrlRespuesta = "https://sqs.respuesta.url";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(propertiesValidacion.queueUrl()).thenReturn(queueUrlValidacion);
        when(propertiesRespuesta.queueUrl()).thenReturn(queueUrlRespuesta);

        sender = new SQSSender<>(
                propertiesValidacion,
                propertiesRespuesta,
                clientValidacion,
                clientRespuesta,
                objectMapper
        );
    }

    @Test
    void debeEnviarMensajeDeValidacion() {
        // Arrange
        DummyMensaje mensaje = new DummyMensaje("123", "mensaje de prueba");
        SendMessageResponse response = SendMessageResponse.builder()
                .messageId("msg-123")
                .build();

        CompletableFuture<SendMessageResponse> futureResponse = CompletableFuture.completedFuture(response);

        when(clientValidacion.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(futureResponse);

        // Act
        Mono<String> resultado = sender.iniciarValidacion(mensaje);

        // Assert
        StepVerifier.create(resultado)
                .expectNext("msg-123")
                .verifyComplete();
    }

    @Test
    void debeEnviarMensajeDeRespuesta() {
        // Arrange
        DummyMensaje mensaje = new DummyMensaje("456", "respuesta de prueba");
        SendMessageResponse response = SendMessageResponse.builder()
                .messageId("msg-456")
                .build();

        CompletableFuture<SendMessageResponse> futureResponse = CompletableFuture.completedFuture(response);

        when(clientRespuesta.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(futureResponse);

        // Act
        Mono<String> resultado = sender.responder(mensaje);

        // Assert
        StepVerifier.create(resultado)
                .expectNext("msg-456")
                .verifyComplete();
    }

    // Dummy DTO para pruebas
    static class DummyMensaje {
        private String id;
        private String contenido;

        public DummyMensaje(String id, String contenido) {
            this.id = id;
            this.contenido = contenido;
        }

        public String getId() { return id; }
        public String getContenido() { return contenido; }
    }
}

