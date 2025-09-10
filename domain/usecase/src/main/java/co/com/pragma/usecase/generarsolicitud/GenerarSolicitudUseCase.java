package co.com.pragma.usecase.generarsolicitud;

import co.com.pragma.model.solicitud.SolicitudPrestamo;
import co.com.pragma.model.solicitud.common.ex.ConflictoException;
import co.com.pragma.model.solicitud.common.ex.NegocioException;
import co.com.pragma.model.solicitud.enums.Estado;
import co.com.pragma.model.solicitud.gateways.NotificacionGateway;
import co.com.pragma.model.solicitud.gateways.SolicitudPrestamoGateway;
import co.com.pragma.model.solicitud.gateways.TipoPrestamoGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.logging.Level;
import java.util.logging.Logger;

import static co.com.pragma.model.solicitud.enums.Estado.obtenerEstadosFinalizados;

@RequiredArgsConstructor
public class GenerarSolicitudUseCase {
    private final TipoPrestamoGateway tipoPrestamoGateway;
    private final SolicitudPrestamoGateway solicitudPrestamoGateway;
    private final NotificacionGateway<SolicitudPrestamo> notificacionGateway;
    private static final Logger logger = Logger.getLogger(GenerarSolicitudUseCase.class.getName());
    public static final String NO_EXISTE_TIPO_PRESTAMO = "El tipo de prestamo (%s) no existe";
    public static final String EXISTE_SOLICITUD_ACTIVA = "Actualmente tiene una solicitud activa por el mismo tipo de prestamo";

    public Mono<SolicitudPrestamo> ejecutar(SolicitudPrestamo solicitudPrestamo) {
        return validarExistenciaSolicitud(solicitudPrestamo)
                .flatMap(solicitudAjustada -> solicitudPrestamoGateway.guardar(solicitudAjustada)
                        .flatMap(solicitudGuardada -> solicitudPrestamoGateway
                                .obtenerDeudaTotalMensualSolicitudesAprobadas(solicitudGuardada.getCorreo())
                                .map(deudaTotalMensual -> solicitudAjustada.toBuilder()
                                        .id(solicitudGuardada.getId())
                                        .deudaTotalMensualSolicitudesAprobadas(deudaTotalMensual)
                                        .build())))
                .doOnSuccess(solicitud -> {
                            if (Boolean.TRUE.equals(solicitud.getTipoPrestamo().getValidacionAutomatica())){
                                notificacionGateway.iniciarValidacion(solicitud)
                                        .subscribeOn(Schedulers.boundedElastic())
                                        .subscribe(
                                                messageId -> logger.info("Mensaje enviado correctamente. ID: " + messageId),
                                                error -> logger.log(Level.SEVERE, "Error enviando notificación", error)
                                        );
                            }
                        }
                );
    }

    private Mono<SolicitudPrestamo> validarExistenciaSolicitud(SolicitudPrestamo solicitudPrestamo){
        return tipoPrestamoGateway.encontrarPorId(solicitudPrestamo.getTipoPrestamoId())
                .flatMap(tipoPrestamo -> solicitudPrestamoGateway
                        .existePorEmailYTipoPrestamoIdSinFinalizar(
                                solicitudPrestamo.getCorreo(),
                                solicitudPrestamo.getTipoPrestamoId(),
                                obtenerEstadosFinalizados().stream().map(Estado::getId).toList())
                        .flatMap(existe -> {
                            if (Boolean.FALSE.equals(existe)){
                                SolicitudPrestamo solicitudAjustada = solicitudPrestamo.toBuilder().tipoPrestamo(tipoPrestamo).build();
                                if (Boolean.FALSE.equals(tipoPrestamo.getValidacionAutomatica())){
                                    solicitudAjustada = solicitudAjustada.toBuilder()
                                            .estadoId(Estado.REVISION.getId())
                                            .estado(Estado.REVISION)
                                            .build();
                                }
                                return Mono.just(solicitudAjustada);
                            }
                            return Mono.error(new ConflictoException(EXISTE_SOLICITUD_ACTIVA));
                        }))
                .switchIfEmpty(Mono.error(new NegocioException(
                        NO_EXISTE_TIPO_PRESTAMO.formatted(solicitudPrestamo.getTipoPrestamoId()))));
    }
}
