package co.com.pragma.usecase.generarsolicitud;

import co.com.pragma.model.solicitud.SolicitudPrestamo;
import co.com.pragma.model.solicitud.common.ex.ConflictoException;
import co.com.pragma.model.solicitud.common.ex.NegocioException;
import co.com.pragma.model.solicitud.enums.Estado;
import co.com.pragma.model.solicitud.gateways.SolicitudPrestamoGateway;
import co.com.pragma.model.solicitud.gateways.TipoPrestamoGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import static co.com.pragma.model.solicitud.enums.Estado.obtenerEstadosFinalizados;

@RequiredArgsConstructor
public class GenerarSolicitudUseCase {
    private final TipoPrestamoGateway tipoPrestamoGateway;
    private final SolicitudPrestamoGateway solicitudPrestamoGateway;
    public static final String NO_EXISTE_TIPO_PRESTAMO = "El tipo de prestamo (%s) no existe";
    public static final String EXISTE_SOLICITUD_ACTIVA = "Actualmente tiene una solicitud activa por el mismo tipo de prestamo";

    public Mono<SolicitudPrestamo> ejecutar(SolicitudPrestamo solicitudPrestamo) {
        return tipoPrestamoGateway.existePorId(solicitudPrestamo.getTipoPrestamoId())
                .flatMap(existe -> {
                    if (Boolean.TRUE.equals(existe)) return solicitudPrestamoGateway.existePorEmailYTipoPrestamoIdSinFinalizar(
                            solicitudPrestamo.getCorreo(),
                            solicitudPrestamo.getTipoPrestamoId(),
                            obtenerEstadosFinalizados().stream()
                                    .map(Estado::getId)
                                    .toList());
                    return Mono.error(new NegocioException(
                            NO_EXISTE_TIPO_PRESTAMO.formatted(solicitudPrestamo.getTipoPrestamoId())));
                })
                .flatMap(existeSolicitud -> {
                    if (Boolean.TRUE.equals(existeSolicitud)) return Mono.error(new ConflictoException(EXISTE_SOLICITUD_ACTIVA));
                    return solicitudPrestamoGateway.guardar(solicitudPrestamo);
                });
    }
}
