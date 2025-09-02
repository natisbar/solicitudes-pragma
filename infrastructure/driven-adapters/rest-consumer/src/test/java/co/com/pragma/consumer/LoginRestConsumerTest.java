package co.com.pragma.consumer;

import co.com.pragma.consumer.usuario.LoginRestConsumer;
import co.com.pragma.model.solicitud.common.ex.IndisponibilidadException;
import co.com.pragma.model.solicitud.common.ex.NegocioException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

class LoginRestConsumerTest {

    private static LoginRestConsumer loginRestConsumer;
    private static MockWebServer mockBackEnd;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();

        var webClient = WebClient.builder()
                .baseUrl(mockBackEnd.url("/").toString())
                .build();

        loginRestConsumer = new LoginRestConsumer(webClient, "correo@test.com", "12345");
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    @DisplayName("Debe retornar token correctamente cuando la API responde 200")
    void validateObtenerTokenOk() {
        mockBackEnd.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value())
                .setBody("token-abc-123"));

        Mono<String> response = loginRestConsumer.obtenerToken();

        StepVerifier.create(response)
                .expectNext("token-abc-123")
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe lanzar NegocioException cuando recibe 400")
    void validateBadRequest() {
        mockBackEnd.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.BAD_REQUEST.value())
                .setBody("Credenciales inválidas"));

        Mono<String> response = loginRestConsumer.obtenerToken();

        StepVerifier.create(response)
                .expectErrorMatches(throwable ->
                        throwable instanceof NegocioException &&
                                throwable.getMessage().contains("Credenciales inválidas"))
                .verify();
    }

    @Test
    @DisplayName("Debe lanzar IndisponibilidadException cuando recibe 500")
    void validateInternalServerError() {
        mockBackEnd.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setBody("Error interno del servidor"));

        Mono<String> response = loginRestConsumer.obtenerToken();

        StepVerifier.create(response)
                .expectErrorMatches(throwable ->
                        throwable instanceof IndisponibilidadException &&
                                throwable.getMessage().contains(LoginRestConsumer.ERROR_CONSUMO_SERVICIO_REST_USUARIO))
                .verify();
    }
}
