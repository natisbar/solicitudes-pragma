package co.com.pragma.api;

import co.com.pragma.api.dto.PrestamoSolicitudActualizarDto;
import co.com.pragma.api.dto.PrestamoSolicitudDto;
import co.com.pragma.api.exception.ManejadorGlobalErrores;
import co.com.pragma.api.mapper.FiltroSolicitudMapper;
import co.com.pragma.api.mapper.SolicitudMapper;
import co.com.pragma.api.seguridad.TestSecurityConfig;
import co.com.pragma.api.seguridad.config.SecurityHeadersConfig;
import co.com.pragma.api.validador.ValidacionManejador;
import co.com.pragma.model.solicitud.PaginacionData;
import co.com.pragma.model.solicitud.SolicitudPrestamo;
import co.com.pragma.model.solicitud.enums.Estado;
import co.com.pragma.usecase.generarsolicitud.ActualizarEstadoSolicitudUseCase;
import co.com.pragma.usecase.generarsolicitud.GenerarSolicitudUseCase;
import co.com.pragma.usecase.generarsolicitud.ObtenerSolicitudUseCase;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, SolicitudHandler.class, ValidacionManejador.class, Validator.class
        , GenerarSolicitudUseCase.class, SolicitudMapper.class, ManejadorGlobalErrores.class, FiltroSolicitudMapper.class
        , SecurityHeadersConfig.class, ActualizarEstadoSolicitudUseCase.class})
@WebFluxTest
@Import(TestSecurityConfig.class)
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private GenerarSolicitudUseCase generarSolicitudUseCase;

    @MockitoBean
    private ActualizarEstadoSolicitudUseCase actualizarEstadoSolicitudUseCase;

    @MockitoBean
    private ObtenerSolicitudUseCase obtenerSolicitudUseCase;

    @Test
    void testListenPOSTUseCase_error400() {
        PrestamoSolicitudDto dto = new PrestamoSolicitudDto(
                "1000000",
                null,
                "1"
        );

        webTestClient
                .post()
                .uri("/v1/solicitudes")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .consumeWith(result -> {
                    String response = new String(result.getResponseBody(), StandardCharsets.UTF_8);
                    System.out.println("Respuesta: " + response);
                })
                .jsonPath("$.estado").isEqualTo(400)
                .jsonPath("$.mensaje").isEqualTo("plazo: El plazo es obligatorio y no puede estar vacio");
    }

    @Test
    void testListenPOSTUseCase_error500() {
        PrestamoSolicitudDto dto = new PrestamoSolicitudDto(
                "1000000",
                "12",
                "1"
        );

        when(generarSolicitudUseCase.ejecutar(any(SolicitudPrestamo.class))).thenReturn(Mono.error(new RuntimeException("error")));

        webTestClient.post()
                .uri("/v1/solicitudes")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin")
                .expectBody()
                .consumeWith(result -> {
                    String response = new String(result.getResponseBody(), StandardCharsets.UTF_8);
                    System.out.println("Respuesta: " + response);
                })
                .jsonPath("$.estado").isEqualTo(500)
                .jsonPath("$.mensaje").isEqualTo("Ocurrió un error inesperado, por favor comuniquese comuniquese con el administrador");
    }

    @Test
    void testListenPOSTUseCase_ok200() {
        PrestamoSolicitudDto dto = new PrestamoSolicitudDto(
                "1000000",
                "12",
                "1"
        );

        SolicitudPrestamo solicitudPrestamo = SolicitudPrestamo.builder()
                .monto(BigDecimal.valueOf(1000000L))
                .plazo(12)
                .tipoPrestamoId(1L)
                .estadoId(Estado.PENDIENTE.getId())
                .build();

        when(generarSolicitudUseCase.ejecutar(any(SolicitudPrestamo.class))).thenReturn(Mono.just(solicitudPrestamo));
        when(generarSolicitudUseCase.ejecutar(any(SolicitudPrestamo.class))).thenReturn(Mono.just(solicitudPrestamo));

        webTestClient.post()
                .uri("/v1/solicitudes")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody();
    }

    @Test
    void testListenPUTUseCase_ok200() {
        PrestamoSolicitudActualizarDto dto = new PrestamoSolicitudActualizarDto(
                "1",
                "APROBADO"
        );

        when(actualizarEstadoSolicitudUseCase.ejecutar(any(SolicitudPrestamo.class))).thenReturn(Mono.just("Actualizado correctamente"));

        webTestClient.put()
                .uri("/v1/solicitudes")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody();
    }

    @Test
    void testListenGETUseCase_ok() {
        // Arrange
        PaginacionData<SolicitudPrestamo> paginacion = new PaginacionData<>(
                List.of(SolicitudPrestamo.builder()
                        .monto(BigDecimal.valueOf(1000000L))
                        .plazo(12)
                        .tipoPrestamoId(1L)
                        .estadoId(Estado.PENDIENTE.getId())
                        .build()),
                1, // totalElementos
                1  // totalPaginas
        );

        when(obtenerSolicitudUseCase.obtenerPorSolicitudesPendientes(any()))
                .thenReturn(Mono.just(paginacion));

        // Act & Assert
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/solicitudes")
                        .queryParam("tipoPrestamoId", "1")
                        .queryParam("correo", "correo@test.com")
                        .queryParam("pagina", "1")
                        .queryParam("tamano", "10")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.datos[0].monto").isEqualTo(1000000)
                .jsonPath("$.datos[0].plazo").isEqualTo(12)
                .jsonPath("$.totalElementos").isEqualTo(1)
                .jsonPath("$.totalPaginas").isEqualTo(1);
    }

}
