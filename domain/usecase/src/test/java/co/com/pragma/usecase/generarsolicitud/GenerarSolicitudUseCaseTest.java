package co.com.pragma.usecase.generarsolicitud;

import co.com.pragma.model.solicitud.SolicitudPrestamo;
import co.com.pragma.model.solicitud.TipoPrestamo;
import co.com.pragma.model.solicitud.common.ex.NegocioException;
import co.com.pragma.model.solicitud.enums.Estado;
import co.com.pragma.model.solicitud.gateways.NotificacionGateway;
import co.com.pragma.model.solicitud.gateways.SolicitudPrestamoGateway;
import co.com.pragma.model.solicitud.gateways.TipoPrestamoGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static co.com.pragma.model.solicitud.enums.Estado.obtenerEstadosFinalizados;
import static co.com.pragma.usecase.generarsolicitud.GenerarSolicitudUseCase.EXISTE_SOLICITUD_ACTIVA;
import static co.com.pragma.usecase.generarsolicitud.GenerarSolicitudUseCase.NO_EXISTE_TIPO_PRESTAMO;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerarSolicitudUseCaseTest {
    @InjectMocks
    GenerarSolicitudUseCase generarSolicitudUseCase;
    @Mock
    TipoPrestamoGateway tipoPrestamoGateway;
    @Mock
    SolicitudPrestamoGateway solicitudPrestamoGateway;
    @Mock
    NotificacionGateway<SolicitudPrestamo> notificacionGateway;

    @Test
    void debeCrearElUsuario() {
        SolicitudPrestamo solicitudPrestamo = SolicitudPrestamo.builder()
                .tipoPrestamoId(1L).correo("email@email.com").build();
        TipoPrestamo tipoPrestamo = TipoPrestamo.builder().id(1L).validacionAutomatica(false).build();

        when(solicitudPrestamoGateway.obtenerDeudaTotalMensualSolicitudesAprobadas(anyString())).thenReturn(Mono.just(BigDecimal.ZERO));
        when(tipoPrestamoGateway.encontrarPorId(solicitudPrestamo.getTipoPrestamoId())).thenReturn(Mono.just(tipoPrestamo));
        when(solicitudPrestamoGateway.existePorEmailYTipoPrestamoIdSinFinalizar(solicitudPrestamo.getCorreo(),
                solicitudPrestamo.getTipoPrestamoId(),
                obtenerEstadosFinalizados().stream()
                        .map(Estado::getId)
                        .toList())).thenReturn(Mono.just(false));
        when(solicitudPrestamoGateway.guardar(any(SolicitudPrestamo.class))).thenReturn(Mono.just(solicitudPrestamo.toBuilder().id(1L).build()));

        Mono<SolicitudPrestamo> result = generarSolicitudUseCase.ejecutar(solicitudPrestamo);

        StepVerifier.create(result)
                .expectNextMatches(respuesta -> respuesta.getId().equals(1L))
                .verifyComplete();
    }

    @Test
    void debeCrearElUsuario_cuandoValidacionAutomaticaEsTrueTodoOk() {
        SolicitudPrestamo solicitudPrestamo = SolicitudPrestamo.builder()
                .tipoPrestamoId(1L).correo("email@email.com").build();
        TipoPrestamo tipoPrestamo = TipoPrestamo.builder().id(1L).validacionAutomatica(true).build();

        when(solicitudPrestamoGateway.obtenerDeudaTotalMensualSolicitudesAprobadas(anyString())).thenReturn(Mono.just(BigDecimal.ZERO));
        when(tipoPrestamoGateway.encontrarPorId(solicitudPrestamo.getTipoPrestamoId())).thenReturn(Mono.just(tipoPrestamo));
        when(notificacionGateway.iniciarValidacion(any(SolicitudPrestamo.class))).thenReturn(Mono.just("1234"));
        when(solicitudPrestamoGateway.existePorEmailYTipoPrestamoIdSinFinalizar(solicitudPrestamo.getCorreo(),
                solicitudPrestamo.getTipoPrestamoId(),
                obtenerEstadosFinalizados().stream()
                        .map(Estado::getId)
                        .toList())).thenReturn(Mono.just(false));
        when(solicitudPrestamoGateway.guardar(any(SolicitudPrestamo.class))).thenReturn(Mono.just(solicitudPrestamo.toBuilder().id(1L).build()));

        Mono<SolicitudPrestamo> result = generarSolicitudUseCase.ejecutar(solicitudPrestamo);

        StepVerifier.create(result)
                .expectNextMatches(respuesta -> respuesta.getId().equals(1L))
                .verifyComplete();
    }

    @Test
    void debeCrearElUsuario_cuandoValidacionAutomaticaEsTrueConError() {
        SolicitudPrestamo solicitudPrestamo = SolicitudPrestamo.builder()
                .tipoPrestamoId(1L).correo("email@email.com").build();
        TipoPrestamo tipoPrestamo = TipoPrestamo.builder().id(1L).validacionAutomatica(true).build();

        when(solicitudPrestamoGateway.obtenerDeudaTotalMensualSolicitudesAprobadas(anyString())).thenReturn(Mono.just(BigDecimal.ZERO));
        when(tipoPrestamoGateway.encontrarPorId(solicitudPrestamo.getTipoPrestamoId())).thenReturn(Mono.just(tipoPrestamo));
        when(notificacionGateway.iniciarValidacion(any(SolicitudPrestamo.class))).thenReturn(Mono.error(new RuntimeException("error")));
        when(solicitudPrestamoGateway.existePorEmailYTipoPrestamoIdSinFinalizar(solicitudPrestamo.getCorreo(),
                solicitudPrestamo.getTipoPrestamoId(),
                obtenerEstadosFinalizados().stream()
                        .map(Estado::getId)
                        .toList())).thenReturn(Mono.just(false));
        when(solicitudPrestamoGateway.guardar(any(SolicitudPrestamo.class))).thenReturn(Mono.just(solicitudPrestamo.toBuilder().id(1L).build()));

        Mono<SolicitudPrestamo> result = generarSolicitudUseCase.ejecutar(solicitudPrestamo);

        StepVerifier.create(result)
                .expectNextMatches(respuesta -> respuesta.getId().equals(1L))
                .verifyComplete();
    }

    @Test
    void debeGenerarExcepcion_tipoPrestamoIdNoExiste() {
        SolicitudPrestamo solicitudPrestamo = SolicitudPrestamo.builder().tipoPrestamoId(1L).correo("email@email.com").build();

        when(tipoPrestamoGateway.encontrarPorId(solicitudPrestamo.getTipoPrestamoId())).thenReturn(Mono.empty());

        Mono<SolicitudPrestamo> result = generarSolicitudUseCase.ejecutar(solicitudPrestamo);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof NegocioException &&
                                throwable.getMessage().equals(NO_EXISTE_TIPO_PRESTAMO.formatted(solicitudPrestamo.getTipoPrestamoId()))
                )
                .verify();
    }

    @Test
    void debeGenerarExcepcion_existeSolicitudConMismoTipoPrestamoActivo() {
        SolicitudPrestamo solicitudPrestamo = SolicitudPrestamo.builder().tipoPrestamoId(1L).correo("email@email.com").build();

        TipoPrestamo tipoPrestamo = TipoPrestamo.builder().id(1L).validacionAutomatica(false).build();

        when(tipoPrestamoGateway.encontrarPorId(solicitudPrestamo.getTipoPrestamoId())).thenReturn(Mono.just(tipoPrestamo));
        when(solicitudPrestamoGateway.existePorEmailYTipoPrestamoIdSinFinalizar(solicitudPrestamo.getCorreo(),
                solicitudPrestamo.getTipoPrestamoId(),
                obtenerEstadosFinalizados().stream()
                        .map(Estado::getId)
                        .toList())).thenReturn(Mono.just(true));
        Mono<SolicitudPrestamo> result = generarSolicitudUseCase.ejecutar(solicitudPrestamo);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof NegocioException &&
                                throwable.getMessage().equals(EXISTE_SOLICITUD_ACTIVA)
                )
                .verify();
    }
}
