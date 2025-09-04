package co.com.pragma.usecase.generarsolicitud;

import co.com.pragma.model.solicitud.FiltroData;
import co.com.pragma.model.solicitud.PaginacionData;
import co.com.pragma.model.solicitud.SolicitudPrestamo;
import co.com.pragma.model.solicitud.Usuario;
import co.com.pragma.model.solicitud.common.ex.NegocioException;
import co.com.pragma.model.solicitud.gateways.SolicitudPrestamoGateway;
import co.com.pragma.model.solicitud.gateways.TipoPrestamoGateway;
import co.com.pragma.model.solicitud.gateways.UsuarioGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static co.com.pragma.usecase.generarsolicitud.GenerarSolicitudUseCase.NO_EXISTE_TIPO_PRESTAMO;
import static java.math.BigDecimal.TEN;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObtenerSolicitudUseCaseTest {
    @InjectMocks
    ObtenerSolicitudUseCase obtenerSolicitudUseCase;
    @Mock
    TipoPrestamoGateway tipoPrestamoGateway;
    @Mock
    UsuarioGateway usuarioGateway;
    @Mock
    private SolicitudPrestamoGateway solicitudPrestamoGateway;

    @Test
    void debeObtenerTodoSinFiltrar() {
        SolicitudPrestamo solicitudPrestamo = SolicitudPrestamo.builder().tipoPrestamoId(1L).correo("email@email.com").build();
        FiltroData filtroData = FiltroData.builder()
                .pagina(1)
                .tamano(8)
                .dataUsuario("token")
                .build();
        Usuario usuario = Usuario.builder().correoElectronico("email@email.com").build();

        when(solicitudPrestamoGateway.obtenerPorEstados(anyList(), anyInt(), anyInt())).thenReturn(Flux.just(solicitudPrestamo));
        when(solicitudPrestamoGateway.contarPorEstados(anyList())).thenReturn(Mono.just(3L));
        when(usuarioGateway.obtenerPorListaCorreos(anyList(), anyString())).thenReturn(Flux.just(usuario));
        when(solicitudPrestamoGateway.obtenerDeudaTotalMensualSolicitudesAprobadas(anyString())).thenReturn(Mono.just(TEN));

        Mono<PaginacionData<SolicitudPrestamo>> result = obtenerSolicitudUseCase.obtenerPorSolicitudesPendientes(filtroData);

        StepVerifier.create(result)
                .expectNextMatches(respuesta -> {
                    SolicitudPrestamo prestamo = respuesta.getDatos().get(0);
                    assertAll(
                            () -> assertEquals("email@email.com", prestamo.getCorreo()),
                            () -> assertEquals(1L, prestamo.getTipoPrestamoId()),
                            () -> assertEquals(3L, respuesta.getTotalElementos()),
                            () -> assertEquals(TEN, prestamo.getDeudaTotalMensualSolicitudesAprobadas())
                    );
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void debeFiltrarPorCorreo() {
        SolicitudPrestamo solicitudPrestamo = SolicitudPrestamo.builder().tipoPrestamoId(1L).correo("email@email.com").build();
        FiltroData filtroData = FiltroData.builder()
                .correo("email@email.com")
                .pagina(1)
                .tamano(8)
                .dataUsuario("token")
                .build();
        Usuario usuario = Usuario.builder().correoElectronico("email@email.com").build();

        when(solicitudPrestamoGateway.obtenerPorEstadosYCorreo(anyList(), anyString(), anyInt(), anyInt())).thenReturn(Flux.just(solicitudPrestamo));
        when(solicitudPrestamoGateway.contarPorEstadosYCorreo(anyList(), anyString())).thenReturn(Mono.just(3L));
        when(usuarioGateway.obtenerPorListaCorreos(anyList(), anyString())).thenReturn(Flux.just(usuario));
        when(solicitudPrestamoGateway.obtenerDeudaTotalMensualSolicitudesAprobadas(anyString())).thenReturn(Mono.just(TEN));

        Mono<PaginacionData<SolicitudPrestamo>> result = obtenerSolicitudUseCase.obtenerPorSolicitudesPendientes(filtroData);

        StepVerifier.create(result)
                .expectNextMatches(respuesta -> {
                    SolicitudPrestamo prestamo = respuesta.getDatos().get(0);
                    assertAll(
                            () -> assertEquals("email@email.com", prestamo.getCorreo()),
                            () -> assertEquals(1L, prestamo.getTipoPrestamoId()),
                            () -> assertEquals(3L, respuesta.getTotalElementos()),
                            () -> assertEquals(TEN, prestamo.getDeudaTotalMensualSolicitudesAprobadas())
                    );
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void debeLanzarExcepcion_tipoPrestamoIdNoExiste() {
        FiltroData filtroData = FiltroData.builder()
                .tipoPrestamoId(1L)
                .pagina(1)
                .tamano(8)
                .build();

        when(tipoPrestamoGateway.existePorId(anyLong())).thenReturn(Mono.just(false));

        Mono<PaginacionData<SolicitudPrestamo>> result = obtenerSolicitudUseCase.obtenerPorSolicitudesPendientes(filtroData);

        StepVerifier.create(result)
        .expectErrorMatches(throwable ->
                throwable instanceof NegocioException &&
                        throwable.getMessage().equals(NO_EXISTE_TIPO_PRESTAMO.formatted(filtroData.getTipoPrestamoId()))
        )
        .verify();
    }

    @Test
    void debeFiltrarPorCorreoYTipoPrestamo() {
        SolicitudPrestamo solicitudPrestamo = SolicitudPrestamo.builder().tipoPrestamoId(1L).correo("email@email.com").build();
        FiltroData filtroData = FiltroData.builder()
                .correo("email@email.com")
                .tipoPrestamoId(1L)
                .pagina(1)
                .tamano(8)
                .dataUsuario("token")
                .build();
        Usuario usuario = Usuario.builder().correoElectronico("email@email.com").build();

        when(tipoPrestamoGateway.existePorId(anyLong())).thenReturn(Mono.just(true));
        when(solicitudPrestamoGateway.obtenerPorEstadosYCorreoYTipoPrestamoId(anyList(), anyString(), anyLong(), anyInt(), anyInt())).thenReturn(Flux.just(solicitudPrestamo));
        when(solicitudPrestamoGateway.contarPorEstadosYCorreoYTipoPrestamoId(anyList(), anyString(), anyLong())).thenReturn(Mono.just(3L));
        when(usuarioGateway.obtenerPorListaCorreos(anyList(), anyString())).thenReturn(Flux.just(usuario));
        when(solicitudPrestamoGateway.obtenerDeudaTotalMensualSolicitudesAprobadas(anyString())).thenReturn(Mono.just(TEN));

        Mono<PaginacionData<SolicitudPrestamo>> result = obtenerSolicitudUseCase.obtenerPorSolicitudesPendientes(filtroData);

        StepVerifier.create(result)
                .expectNextMatches(respuesta -> {
                    SolicitudPrestamo prestamo = respuesta.getDatos().get(0);
                    assertAll(
                            () -> assertEquals("email@email.com", prestamo.getCorreo()),
                            () -> assertEquals(1L, prestamo.getTipoPrestamoId()),
                            () -> assertEquals(3L, respuesta.getTotalElementos()),
                            () -> assertEquals(TEN, prestamo.getDeudaTotalMensualSolicitudesAprobadas())
                    );
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void debeFiltrarPorTipoPrestamo() {
        SolicitudPrestamo solicitudPrestamo = SolicitudPrestamo.builder().tipoPrestamoId(1L).correo("email@email.com").build();
        FiltroData filtroData = FiltroData.builder()
                .tipoPrestamoId(1L)
                .pagina(1)
                .tamano(8)
                .dataUsuario("token")
                .build();
        Usuario usuario = Usuario.builder().correoElectronico("email@email.com").build();

        when(tipoPrestamoGateway.existePorId(anyLong())).thenReturn(Mono.just(true));
        when(solicitudPrestamoGateway.obtenerPorEstadosYTipoPrestamoId(anyList(), anyLong(), anyInt(), anyInt())).thenReturn(Flux.just(solicitudPrestamo));
        when(solicitudPrestamoGateway.contarPorEstadosYTipoPrestamoId(anyList(), anyLong())).thenReturn(Mono.just(3L));
        when(usuarioGateway.obtenerPorListaCorreos(anyList(), anyString())).thenReturn(Flux.just(usuario));
        when(solicitudPrestamoGateway.obtenerDeudaTotalMensualSolicitudesAprobadas(anyString())).thenReturn(Mono.just(TEN));

        Mono<PaginacionData<SolicitudPrestamo>> result = obtenerSolicitudUseCase.obtenerPorSolicitudesPendientes(filtroData);

        StepVerifier.create(result)
                .expectNextMatches(respuesta -> {
                    SolicitudPrestamo prestamo = respuesta.getDatos().get(0);
                    assertAll(
                            () -> assertEquals("email@email.com", prestamo.getCorreo()),
                            () -> assertEquals(1L, prestamo.getTipoPrestamoId()),
                            () -> assertEquals(3L, respuesta.getTotalElementos()),
                            () -> assertEquals(TEN, prestamo.getDeudaTotalMensualSolicitudesAprobadas())
                    );
                    return true;
                })
                .verifyComplete();
    }


//    @Test
//    void debeGenerarExcepcion_existeSolicitudConMismoTipoPrestamoActivo() {
//        SolicitudPrestamo solicitudPrestamo = SolicitudPrestamo.builder().tipoPrestamoId(1L).correo("email@email.com").build();
//
//        when(tipoPrestamoGateway.existePorId(solicitudPrestamo.getTipoPrestamoId())).thenReturn(Mono.just(true));
//        when(solicitudPrestamoGateway.existePorEmailYTipoPrestamoIdSinFinalizar(solicitudPrestamo.getCorreo(),
//                solicitudPrestamo.getTipoPrestamoId(),
//                obtenerEstadosFinalizados().stream()
//                        .map(Estado::getId)
//                        .toList())).thenReturn(Mono.just(true));
//        Mono<SolicitudPrestamo> result = generarSolicitudUseCase.ejecutar(solicitudPrestamo);
//
//        StepVerifier.create(result)
//                .expectErrorMatches(throwable ->
//                        throwable instanceof NegocioException &&
//                                throwable.getMessage().equals(EXISTE_SOLICITUD_ACTIVA)
//                )
//                .verify();
//    }
}
