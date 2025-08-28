package co.com.pragma.usecase.generarsolicitud;

import co.com.pragma.model.solicitud.FiltroData;
import co.com.pragma.model.solicitud.PaginacionData;
import co.com.pragma.model.solicitud.SolicitudPrestamo;
import co.com.pragma.model.solicitud.common.ex.NegocioException;
import co.com.pragma.model.solicitud.enums.Estado;
import co.com.pragma.model.solicitud.gateways.SolicitudPrestamoGateway;
import co.com.pragma.model.solicitud.gateways.TipoPrestamoGateway;
import co.com.pragma.model.solicitud.gateways.UsuarioGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static co.com.pragma.usecase.generarsolicitud.GenerarSolicitudUseCase.NO_EXISTE_TIPO_PRESTAMO;

@RequiredArgsConstructor
public class ObtenerSolicitudUseCase {
    private final SolicitudPrestamoGateway solicitudPrestamoGateway;
    private final TipoPrestamoGateway tipoPrestamoGateway;
    private final UsuarioGateway usuarioGateway;

    public Mono<PaginacionData<SolicitudPrestamo>> obtenerPorSolicitudesPendientes(FiltroData filtroData){
        return Mono.just(filtroData)
                .flatMap(filtro -> {
                    List<Long> estadosId = Estado.obtenerEstadosSinFinalizar().stream()
                            .map(Estado::getId).toList();
                    if (filtro.todoNull()){
                        return solicitudPrestamoGateway.obtenerPorEstados(estadosId, filtro.getPagina(), filtro.getTamano())
                                .collectList()
                                .flatMap(listaSolicitudes -> solicitudPrestamoGateway.contarPorEstados(estadosId)
                                        .flatMap(total -> construirDatosPaginacion(listaSolicitudes, total, filtro.getTamano())));
                    }
                    if (filtro.existeSoloCorreo()){
                        return solicitudPrestamoGateway.obtenerPorEstadosYCorreo(estadosId, filtro.getCorreo(), filtro.getPagina(), filtro.getTamano())
                                .collectList()
                                .flatMap(listaSolicitudes -> solicitudPrestamoGateway.contarPorEstadosYCorreo(estadosId, filtro.getCorreo())
                                        .flatMap(total -> construirDatosPaginacion(listaSolicitudes, total, filtro.getTamano())));
                    }
                    return obtenerCuandoTipoPrestamoExiste(filtro, estadosId);
                });
    }

    private Mono<PaginacionData<SolicitudPrestamo>> obtenerCuandoTipoPrestamoExiste(FiltroData filtro, List<Long> estadosId){
        return tipoPrestamoGateway.existePorId(filtro.getTipoPrestamoId())
                .flatMap(existe -> {
                    if (Boolean.FALSE.equals(existe)) return Mono.error(new NegocioException(NO_EXISTE_TIPO_PRESTAMO.formatted(filtro.getTipoPrestamoId())));
                    if (filtro.todoNoNull()){
                        return solicitudPrestamoGateway.obtenerPorEstadosYCorreoYTipoPrestamoId(estadosId, filtro.getCorreo(),
                                filtro.getTipoPrestamoId(), filtro.getPagina(), filtro.getTamano())
                                .collectList()
                                .flatMap(listaSolicitudes -> solicitudPrestamoGateway.contarPorEstadosYCorreoYTipoPrestamoId(estadosId, filtro.getCorreo(), filtro.getTipoPrestamoId())
                                        .flatMap(total -> construirDatosPaginacion(listaSolicitudes, total, filtro.getTamano())));
                    }
                    return solicitudPrestamoGateway.obtenerPorEstadosYTipoPrestamoId(estadosId, filtro.getTipoPrestamoId(),
                            filtro.getPagina(), filtro.getTamano())
                            .collectList()
                            .flatMap(listaSolicitudes -> solicitudPrestamoGateway.contarPorEstadosYTipoPrestamoId(estadosId, filtro.getTipoPrestamoId())
                                    .flatMap(total -> construirDatosPaginacion(listaSolicitudes, total, filtro.getTamano())));
                });
    }

    private Mono<PaginacionData<SolicitudPrestamo>> construirDatosPaginacion(List<SolicitudPrestamo> listaSolicitudes, long totalElementos,
                                                                             long elementosPorPagina){
        return usuarioGateway.obtenerPorListaCorreos(obtenerListaCorreos(listaSolicitudes))
                .collectList()
                .flatMapMany(listaUsuarios -> Flux.fromIterable(listaSolicitudes)
                        .map(solicitud -> solicitud.toBuilder().solicitante(listaUsuarios.stream()
                                        .filter(usuario -> usuario.getCorreoElectronico()
                                                .equals(solicitud.getCorreo()))
                                        .findFirst().orElse(null)).build()))
                .collectList()
                .map(solicitudes -> PaginacionData.<SolicitudPrestamo>builder()
                        .datos(solicitudes)
                        .totalElementos(totalElementos)
                        .totalPaginas((long) Math.ceil((double) totalElementos / elementosPorPagina))
                        .build());
    }

    private List<String> obtenerListaCorreos(List<SolicitudPrestamo> listaSolicitudes){
        return listaSolicitudes.stream().map(SolicitudPrestamo::getCorreo).toList();
    }
}
