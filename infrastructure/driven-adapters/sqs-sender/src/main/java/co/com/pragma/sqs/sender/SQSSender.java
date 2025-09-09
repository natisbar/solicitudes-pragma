package co.com.pragma.sqs.sender;

import co.com.pragma.model.solicitud.gateways.NotificacionGateway;
import co.com.pragma.sqs.sender.config.SQSSenderRespuestaProperties;
import co.com.pragma.sqs.sender.config.SQSSenderValidacionProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.UUID;

@Service
@Log4j2
public class SQSSender<T> implements NotificacionGateway<T> {
    private final SQSSenderValidacionProperties propertiesValidacion;
    private final SQSSenderRespuestaProperties propertiesRespuesta;
    private final SqsAsyncClient clientValidacion;
    private final SqsAsyncClient clientRespuesta;
    private final ObjectMapper objectMapper;

    public SQSSender(SQSSenderValidacionProperties propertiesValidacion, SQSSenderRespuestaProperties propertiesRespuesta, @Qualifier("sqsAsyncClientValidacion") SqsAsyncClient clientValidacion,
                     @Qualifier("sqsAsyncClientRespuesta") SqsAsyncClient clientRespuesta, ObjectMapper objectMapper) {
        this.propertiesValidacion = propertiesValidacion;
        this.propertiesRespuesta = propertiesRespuesta;
        this.clientValidacion = clientValidacion;
        this.clientRespuesta = clientRespuesta;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<String> iniciarValidacion(T objetoMensaje) {
        return Mono.fromCallable(() -> buildRequest(objetoMensaje, "prestamos", propertiesValidacion.queueUrl()))
                .flatMap(request -> Mono.fromFuture(clientValidacion.sendMessage(request)))
                .doOnNext(response -> log.debug("Message sent {}", response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    @Override
    public Mono<String> responder(T objetoMensaje) {
        return  Mono.fromCallable(() -> buildRequest(objetoMensaje, "respuesta", propertiesRespuesta.queueUrl()))
                .flatMap(request -> Mono.fromFuture(clientRespuesta.sendMessage(request)))
                .doOnNext(response -> log.debug("Message sent {}", response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(T objetoMensaje, String idGrupo, String queueUrl) {
        return SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(convertirAString(objetoMensaje))
                .messageGroupId(idGrupo)
                .messageDeduplicationId(UUID.randomUUID().toString())
                .build();
    }

    private String convertirAString(T objetoMensaje){
        try {
            return objectMapper.writeValueAsString(objetoMensaje);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}
