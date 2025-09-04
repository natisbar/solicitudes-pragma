package co.com.pragma.consumer;


import co.com.pragma.consumer.usuario.UsuarioRestConsumer;
import co.com.pragma.model.solicitud.common.ex.IndisponibilidadException;
import co.com.pragma.model.solicitud.common.ex.NegocioException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.List;


class UsuarioRestConsumerTest {

    private static MockWebServer mockBackEnd;
    private static UsuarioRestConsumer usuarioRestConsumer;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();

        var webClient = WebClient.builder()
                .baseUrl(mockBackEnd.url("/").toString())
                .build();

        usuarioRestConsumer = new UsuarioRestConsumer(webClient);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    @DisplayName("Debe retornar lista de usuarios cuando recibe 200")
    void validateTestGet() {
        List<String> correos = List.of("correo1@gmail.com", "correo2@gmail.com");

        mockBackEnd.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value())
                .setBody("[{\"nombres\" : \"natalia\"}]"));

        var response = usuarioRestConsumer.obtenerPorListaCorreos(correos, "sasdsa2323");

        StepVerifier.create(response)
                .expectNextMatches(usuario -> usuario.getNombres().equals("natalia"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe lanzar NegocioException cuando recibe 400")
    void validateBadRequest() {
        List<String> correos = List.of("correo1@gmail.com");

        mockBackEnd.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.BAD_REQUEST.value())
                .setBody("Correo no válido"));

        var response = usuarioRestConsumer.obtenerPorListaCorreos(correos, "sasdsa2323");

        StepVerifier.create(response)
                .expectErrorMatches(throwable ->
                        throwable instanceof NegocioException &&
                                throwable.getMessage().contains("Correo no válido"))
                .verify();
    }

    @Test
    @DisplayName("Debe lanzar IndisponibilidadException cuando recibe 401")
    void validateUnauthorized() {
        List<String> correos = List.of("correo1@gmail.com");

        mockBackEnd.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.UNAUTHORIZED.value())
                .setBody("Token inválido o expirado"));

        var response = usuarioRestConsumer.obtenerPorListaCorreos(correos, "sasdsa2323");

        StepVerifier.create(response)
                .expectErrorMatches(throwable ->
                        throwable instanceof IndisponibilidadException &&
                                throwable.getMessage().contains("Token inválido o expirado"))
                .verify();
    }

    @Test
    @DisplayName("Debe lanzar IndisponibilidadException cuando recibe 500")
    void validateInternalServerError() {
        List<String> correos = List.of("correo1@gmail.com");

        mockBackEnd.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setBody("Error interno del servidor"));

        var response = usuarioRestConsumer.obtenerPorListaCorreos(correos, "sasdsa2323");

        StepVerifier.create(response)
                .expectErrorMatches(throwable ->
                        throwable instanceof IndisponibilidadException &&
                                throwable.getMessage().contains(UsuarioRestConsumer.ERROR_CONSUMO_SERVICIO_REST_USUARIO))
                .verify();
    }
}