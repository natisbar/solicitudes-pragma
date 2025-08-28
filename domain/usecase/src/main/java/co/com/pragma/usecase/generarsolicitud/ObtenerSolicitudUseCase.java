package co.com.pragma.usecase.generarsolicitud;

import co.com.pragma.model.solicitud.FiltroData;
import co.com.pragma.model.solicitud.PaginacionData;
import co.com.pragma.model.solicitud.SolicitudPrestamo;
import co.com.pragma.model.solicitud.common.ex.NegocioException;
import co.com.pragma.model.solicitud.enums.Estado;
import co.com.pragma.model.solicitud.gateways.SolicitudPrestamoGateway;
import co.com.pragma.model.solicitud.gateways.TipoPrestamoGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

import static co.com.pragma.usecase.generarsolicitud.GenerarSolicitudUseCase.NO_EXISTE_TIPO_PRESTAMO;

@RequiredArgsConstructor
public class ObtenerSolicitudUseCase {
    private final SolicitudPrestamoGateway solicitudPrestamoGateway;
    private final TipoPrestamoGateway tipoPrestamoGateway;

    public Mono<PaginacionData<SolicitudPrestamo>> obtenerPorSolicitudesPendientes(FiltroData filtroData){
        return Mono.just(filtroData)
                .flatMap(filtro -> {
                    List<Long> estadosId = Estado.obtenerEstadosSinFinalizar().stream()
                            .map(Estado::getId).toList();
                    if (filtro.todoNull()){
                        return solicitudPrestamoGateway.obtenerPorEstados(estadosId, filtro.getPagina(), filtro.getTamano())
                                .collectList()
                                .flatMap(listaSolicitudes -> solicitudPrestamoGateway.contarPorEstados(estadosId)
                                        .map(total -> construirDatosPaginacion(listaSolicitudes, total, filtro.getTamano())));
                    }
                    if (filtro.existeSoloCorreo()){
                        return solicitudPrestamoGateway.obtenerPorEstadosYCorreo(estadosId, filtro.getCorreo(), filtro.getPagina(), filtro.getTamano())
                                .collectList()
                                .flatMap(listaSolicitudes -> solicitudPrestamoGateway.contarPorEstadosYCorreo(estadosId, filtro.getCorreo())
                                        .map(total -> construirDatosPaginacion(listaSolicitudes, total, filtro.getTamano())));
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
                                        .map(total -> construirDatosPaginacion(listaSolicitudes, total, filtro.getTamano())));
                    }
                    return solicitudPrestamoGateway.obtenerPorEstadosYTipoPrestamoId(estadosId, filtro.getTipoPrestamoId(),
                            filtro.getPagina(), filtro.getTamano())
                            .collectList()
                            .flatMap(listaSolicitudes -> solicitudPrestamoGateway.contarPorEstadosYTipoPrestamoId(estadosId, filtro.getTipoPrestamoId())
                                    .map(total -> construirDatosPaginacion(listaSolicitudes, total, filtro.getTamano())));
                });
    }

    private PaginacionData<SolicitudPrestamo> construirDatosPaginacion(List<SolicitudPrestamo> listaSolicitudes, long totalElementos,
                                                                             long elementosPorPagina){
        long totalPaginas = (long) Math.ceil((double) totalElementos / elementosPorPagina);
        return PaginacionData.<SolicitudPrestamo>builder()
                .datos(listaSolicitudes)
                .totalElementos(totalElementos)
                .totalPaginas(totalPaginas)
                .build();
    }
}
