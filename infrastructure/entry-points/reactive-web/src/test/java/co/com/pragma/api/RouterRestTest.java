package co.com.pragma.api;

import co.com.pragma.api.exception.ManejadorGlobalErrores;
import co.com.pragma.api.mapper.SolicitudMapper;
import co.com.pragma.api.validador.ValidacionManejador;
import co.com.pragma.model.solicitud.SolicitudPrestamo;
import co.com.pragma.model.solicitud.enums.Estado;
import co.com.pragma.usecase.generarsolicitud.GenerarSolicitudUseCase;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, Handler.class, ValidacionManejador.class, Validator.class
        , GenerarSolicitudUseCase.class, SolicitudMapper.class, ManejadorGlobalErrores.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private GenerarSolicitudUseCase generarSolicitudUseCase;

    @Test
    void testListenPOSTUseCase_error400() {
        String solicitudJson = """
                {
                "monto": 1000000,
                "plazo": null,
                "email": "jandrej100@gmail.com",
                "tipoPrestamoId": 1,
                "estadoId": null,
                "usuarioId": 1
                }""";

        webTestClient.post()
                .uri("/v1/solicitudes")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(solicitudJson)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .consumeWith(result -> {
                    String response = new String(result.getResponseBody(), StandardCharsets.UTF_8);
                    System.out.println("Respuesta: " + response);
                })
                .jsonPath("$.estado").isEqualTo(400)
                .jsonPath("$.mensaje").isEqualTo("plazo: El plazo es obligatorio");
    }

    @Test
    void testListenPOSTUseCase_error500() {
        String solicitudJson = """
                {
                "monto": 1000000,
                "plazo": 12,
                "email": "jandrej100@gmail.com",
                "tipoPrestamoId": 1
                }""";

        when(generarSolicitudUseCase.ejecutar(any(SolicitudPrestamo.class))).thenReturn(Mono.error(new RuntimeException("error")));

        webTestClient.post()
                .uri("/v1/solicitudes")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(solicitudJson)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .consumeWith(result -> {
                    String response = new String(result.getResponseBody(), StandardCharsets.UTF_8);
                    System.out.println("Respuesta: " + response);
                })
                .jsonPath("$.estado").isEqualTo(500)
                .jsonPath("$.mensaje").isEqualTo("Ocurrió un error inesperado, por favor comuniquese comuniquese con el administrador");
    }

    @Test
    void testListenPOSTUseCase_error200() {
        String solicitudJson = """
                {
                "monto": 1000000,
                "plazo": 12,
                "email": "jandrej100@gmail.com",
                "tipoPrestamoId": 1
                }""";

        SolicitudPrestamo solicitudPrestamo = SolicitudPrestamo.builder()
                .monto(BigDecimal.valueOf(1000000L))
                .plazo(12)
                .email("jandrej100@gmail.com")
                .tipoPrestamoId(1L)
                .estadoId(Estado.PENDIENTE.getId())
                .build();

        when(generarSolicitudUseCase.ejecutar(any(SolicitudPrestamo.class))).thenReturn(Mono.just(solicitudPrestamo));

        webTestClient.post()
                .uri("/v1/solicitudes")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(solicitudJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody();
    }
}
