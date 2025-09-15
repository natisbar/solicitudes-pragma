package co.com.pragma.usecase.generarsolicitud;

import co.com.pragma.model.solicitud.SolicitudPrestamo;
import co.com.pragma.model.solicitud.TipoPrestamo;
import co.com.pragma.model.solicitud.Usuario;
import co.com.pragma.model.solicitud.common.ex.NegocioException;
import co.com.pragma.model.solicitud.enums.Estado;
import co.com.pragma.model.solicitud.gateways.PublicacionGateway;
import co.com.pragma.model.solicitud.gateways.SolicitudPrestamoGateway;
import co.com.pragma.model.solicitud.gateways.UsuarioGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static co.com.pragma.usecase.generarsolicitud.ActualizarEstadoSolicitudUseCase.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarEstadoSolicitudUseCaseTest {

    @Mock
    private SolicitudPrestamoGateway solicitudGateway;
    @Mock
    private UsuarioGateway usuarioGateway;
    @Mock
    private PublicacionGateway<SolicitudPrestamo> publicacionGateway;
    @InjectMocks
    private ActualizarEstadoSolicitudUseCase useCase;

    private static final BigDecimal SALARIO_BASE = new BigDecimal("1000");

    @Test
    void errorCuandoEstadoNoEsFinalizado() {
        SolicitudPrestamo solicitud = SolicitudPrestamo.builder()
                .id(1L)
                .estado(Estado.PENDIENTE)
                .build();

        StepVerifier.create(useCase.ejecutar(solicitud))
                .expectErrorMatches(error ->
                        error instanceof NegocioException &&
                                error.getMessage().equals(ESTADO_NO_ES_PARA_FINALIZAR))
                .verify();
    }

    @Test
    void errorCuandoSolicitudNoExiste() {
        SolicitudPrestamo solicitud = SolicitudPrestamo.builder()
                .id(99L)
                .estado(Estado.APROBADO)
                .build();

        when(solicitudGateway.obtenerPorId(99L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.ejecutar(solicitud))
                .expectErrorMatches(error ->
                        error instanceof NegocioException &&
                                error.getMessage().equals(SOLICITUD_NO_EXISTE))
                .verify();
    }

    @Test
    void errorCuandoSolicitudYaEstaFinalizada() {
        SolicitudPrestamo solicitud = SolicitudPrestamo.builder()
                .id(1L)
                .estado(Estado.RECHAZADA)
                .build();

        SolicitudPrestamo encontrada = SolicitudPrestamo.builder()
                .id(1L)
                .estado(Estado.APROBADO)
                .correo("correo@test.com")
                .build();

        Usuario usuario = Usuario.builder()
                .salarioBase(SALARIO_BASE)
                .build();

        when(solicitudGateway.obtenerPorId(1L)).thenReturn(Mono.just(encontrada));
        when(usuarioGateway.obtenerPorListaCorreos(any(), any())).thenReturn(Flux.just(usuario));

        StepVerifier.create(useCase.ejecutar(solicitud))
                .expectErrorMatches(error ->
                        error instanceof NegocioException &&
                                error.getMessage().equals(SOLICITUD_FINALIZADA))
                .verify();
    }

    @Test
    void actualizarAprobadoDentroDelLimite() {
        SolicitudPrestamo solicitud = SolicitudPrestamo.builder()
                .id(1L)
                .estado(Estado.APROBADO)
                .build();

        SolicitudPrestamo encontrada = SolicitudPrestamo.builder()
                .id(1L)
                .estado(Estado.PENDIENTE)
                .monto(new BigDecimal("3000"))
                .plazo(6)
                .correo("user@test.com")
                .tipoPrestamo(TipoPrestamo.builder().tasaInteres(new BigDecimal("10")).build())
                .tipoPrestamoId(10L)
                .build();

        Usuario usuario = Usuario.builder()
                .salarioBase(SALARIO_BASE)
                .build();

        when(solicitudGateway.obtenerPorId(1L)).thenReturn(Mono.just(encontrada));
        when(usuarioGateway.obtenerPorListaCorreos(any(), any())).thenReturn(Flux.just(usuario));
        when(solicitudGateway.actualizarEstado(any())).thenReturn(Mono.empty());
        when(publicacionGateway.publicar(any())).thenReturn(Mono.just("msg-123"));

        StepVerifier.create(useCase.ejecutar(solicitud))
                .expectNext("Solicitud actualizada correctamente")
                .verifyComplete();
    }

    @Test
    void actualizarAprobadoCuandoEstadoPrevisionRevisionManualYNotificacionError() {
        SolicitudPrestamo solicitud = SolicitudPrestamo.builder()
                .id(1L)
                .estado(Estado.APROBADO)
                .build();

        SolicitudPrestamo encontrada = SolicitudPrestamo.builder()
                .id(1L)
                .estado(Estado.REVISION)
                .monto(new BigDecimal("3000"))
                .plazo(6)
                .correo("user@test.com")
                .tipoPrestamo(TipoPrestamo.builder().tasaInteres(new BigDecimal("10")).build())
                .tipoPrestamoId(10L)
                .build();

        Usuario usuario = Usuario.builder()
                .salarioBase(SALARIO_BASE)
                .build();

        when(solicitudGateway.obtenerPorId(1L)).thenReturn(Mono.just(encontrada));
        when(usuarioGateway.obtenerPorListaCorreos(any(), any())).thenReturn(Flux.just(usuario));
        when(solicitudGateway.actualizarEstado(any())).thenReturn(Mono.empty());
        when(publicacionGateway.publicar(any())).thenReturn(Mono.error(new RuntimeException("erros")));

        StepVerifier.create(useCase.ejecutar(solicitud))
                .expectNext("Solicitud actualizada correctamente")
                .verifyComplete();
    }

    @Test
    void actualizarAprobadoExcediendoMonto() {
        SolicitudPrestamo solicitud = SolicitudPrestamo.builder()
                .id(2L)
                .estado(Estado.APROBADO)
                .build();

        SolicitudPrestamo encontrada = SolicitudPrestamo.builder()
                .id(2L)
                .estado(Estado.PENDIENTE)
                .monto(new BigDecimal("6000")) // > 5 * 1000
                .plazo(6)
                .correo("user@test.com")
                .tipoPrestamo(TipoPrestamo.builder().tasaInteres(new BigDecimal("10")).build())
                .tipoPrestamoId(10L)
                .build();

        Usuario usuario = Usuario.builder()
                .salarioBase(SALARIO_BASE)
                .build();

        when(solicitudGateway.obtenerPorId(2L)).thenReturn(Mono.just(encontrada));
        when(usuarioGateway.obtenerPorListaCorreos(any(), any())).thenReturn(Flux.just(usuario));
        when(solicitudGateway.actualizarEstado(any())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.ejecutar(solicitud))
                .expectNext(SOLICITUD_ACTUALIZADA)
                .verifyComplete();
    }

    @Test
    void actualizarRechazadoCorrectamente() {
        SolicitudPrestamo solicitud = SolicitudPrestamo.builder()
                .id(3L)
                .estado(Estado.RECHAZADA)
                .build();

        SolicitudPrestamo encontrada = SolicitudPrestamo.builder()
                .id(3L)
                .estado(Estado.PENDIENTE)
                .monto(new BigDecimal("2000"))
                .plazo(6)
                .correo("user@test.com")
                .tipoPrestamo(TipoPrestamo.builder().tasaInteres(new BigDecimal("10")).build())
                .tipoPrestamoId(10L)
                .build();

        Usuario usuario = Usuario.builder()
                .salarioBase(SALARIO_BASE)
                .build();

        when(solicitudGateway.obtenerPorId(3L)).thenReturn(Mono.just(encontrada));
        when(usuarioGateway.obtenerPorListaCorreos(any(), any())).thenReturn(Flux.just(usuario));
        when(solicitudGateway.actualizarEstado(any())).thenReturn(Mono.empty());
        when(publicacionGateway.publicar(any())).thenReturn(Mono.just("msg-789"));

        StepVerifier.create(useCase.ejecutar(solicitud))
                .expectNext("Solicitud actualizada correctamente")
                .verifyComplete();
    }
}
