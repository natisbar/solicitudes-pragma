package co.com.pragma.sqs.listener.helper;

import co.com.pragma.sqs.listener.SQSProcessor;
import co.com.pragma.sqs.listener.config.SQSProperties;
import co.com.pragma.sqs.listener.mapper.PrestamoSolicitudMapper;
import co.com.pragma.usecase.generarsolicitud.ActualizarEstadoSolicitudUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class SQSListenerTest {

    @Mock
    private SqsAsyncClient asyncClient;
    @Mock
    private ActualizarEstadoSolicitudUseCase actualizarEstadoSolicitudUseCase;
    @Mock
    private GenerarTokenProvisionalService generarTokenProvisionalService;
    @Mock
    private PrestamoSolicitudMapper prestamoSolicitudMapper;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private SQSProperties sqsProperties;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        var message = Message.builder().body("message").build();
        var deleteMessageResponse = DeleteMessageResponse.builder().build();
        var messageResponse = ReceiveMessageResponse.builder().messages(message).build();

        when(asyncClient.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(messageResponse));
        when(asyncClient.deleteMessage(any(DeleteMessageRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(deleteMessageResponse));
    }

    @Test
    void listenerTest() {
        var sqsListener = SQSListener.builder()
                .client(asyncClient)
                .properties(sqsProperties)
                .processor(new SQSProcessor(actualizarEstadoSolicitudUseCase, generarTokenProvisionalService,
                        prestamoSolicitudMapper, objectMapper))
                .operation("operation")
                .build();

        Flux<Void> flow = ReflectionTestUtils.invokeMethod(sqsListener, "listen");
        StepVerifier.create(flow).verifyComplete();
    }
}
