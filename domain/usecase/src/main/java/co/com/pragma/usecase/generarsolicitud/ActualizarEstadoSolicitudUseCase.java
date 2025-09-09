package co.com.pragma.usecase.generarsolicitud;

import co.com.pragma.model.solicitud.SolicitudPrestamo;
import co.com.pragma.model.solicitud.common.ex.NegocioException;
import co.com.pragma.model.solicitud.enums.Estado;
import co.com.pragma.model.solicitud.gateways.NotificacionGateway;
import co.com.pragma.model.solicitud.gateways.SolicitudPrestamoGateway;
import co.com.pragma.model.solicitud.gateways.UsuarioGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class ActualizarEstadoSolicitudUseCase {
    private final SolicitudPrestamoGateway solicitudPrestamoGateway;
    private final UsuarioGateway usuarioGateway;
    private final NotificacionGateway<SolicitudPrestamo> notificacionGateway;
    private static final Logger logger = Logger.getLogger(ActualizarEstadoSolicitudUseCase.class.getName());
    private static final String ESTADO_NO_ES_PARA_FINALIZAR = "El estado no es válido, debe ser RECHAZADO o APROBADO";
    private static final String SOLICITUD_FINALIZADA = "La solicitud ya fue finalizada.";
    private static final String SOLICITUD_NO_EXISTE = "La solicitud a actualizar no existe";
    private static final String SOLICITUD_ACTUALIZADA = "Solicitud actualizada correctamente";
    private static final BigDecimal CANTIDAD_SALARIOS_MAXIMOS = new BigDecimal(5);

    public Mono<String> ejecutar(SolicitudPrestamo solicitudPrestamo) {
        return Mono.just(solicitudPrestamo)
                .flatMap(solicitud -> {
                    List<Estado> estadosFinalizados = Estado.obtenerEstadosFinalizados();
                    if (estadosFinalizados.contains(solicitud.getEstado())){
                        return solicitudPrestamoGateway.obtenerPorId(solicitud.getId())
                                .flatMap(solicitudEncontrada ->
                                                usuarioGateway.obtenerPorListaCorreos(List.of(solicitudEncontrada.getCorreo()), solicitud.getDataUsuario())
                                                        .next()
                                                        .flatMap(usuario -> validarEstadoPrevioYActualizar(solicitud.toBuilder()
                                                                .tipoPrestamoId(solicitudEncontrada.getTipoPrestamoId())
                                                                .tipoPrestamo(solicitudEncontrada.getTipoPrestamo())
                                                                .monto(solicitudEncontrada.getMonto())
                                                                .plazo(solicitudEncontrada.getPlazo())
                                                                .correo(solicitudEncontrada.getCorreo()).solicitante(usuario)
                                                                .build(), solicitudEncontrada.getEstado()))
                                                        .doOnSuccess(solicitudFinal -> {
                                                            if (Estado.obtenerEstadosFinalizados().contains(solicitudFinal.getEstado())){
                                                                notificacionGateway.responder(solicitudFinal)
                                                                        .subscribeOn(Schedulers.boundedElastic())
                                                                        .subscribe(
                                                                                messageId -> logger.info("Mensaje enviado correctamente. ID: " + messageId),
                                                                                error -> logger.log(Level.SEVERE, "Error enviando notificación", error)
                                                                        );
                                                            }
                                                        })
                                                        .thenReturn(SOLICITUD_ACTUALIZADA)
                                )
                                .switchIfEmpty(Mono.error(new NegocioException(SOLICITUD_NO_EXISTE)));
                    }
                    return Mono.error(new NegocioException(ESTADO_NO_ES_PARA_FINALIZAR));
                })
                .doOnError(error -> logger.log(Level.SEVERE, "Error enviando notificación", error));
    }

    private Mono<SolicitudPrestamo> validarEstadoPrevioYActualizar(SolicitudPrestamo solicitudActualizar, Estado estadoPrevio){
        return Mono.just(estadoPrevio)
                .flatMap(estado -> {
                    if (Estado.PENDIENTE.equals(estadoPrevio)){
                        if (Estado.APROBADO.equals(solicitudActualizar.getEstado()) &&
                                solicitudActualizar.getMonto().compareTo(solicitudActualizar.getSolicitante().getSalarioBase().multiply(CANTIDAD_SALARIOS_MAXIMOS)) > 0){
                            return Mono.just(solicitudActualizar.toBuilder().estadoId(Estado.REVISION.getId()).estado(Estado.REVISION).build());
                        }
                        return Mono.just(solicitudActualizar);
                    }
                    else if (Estado.REVISION.equals(estadoPrevio)){
                        return Mono.just(solicitudActualizar);
                    }
                    else return Mono.error(new NegocioException(SOLICITUD_FINALIZADA));
                })
                .flatMap(solicitudFinal -> solicitudPrestamoGateway.actualizarEstado(solicitudFinal)
                        .thenReturn(solicitudFinal));
    }

}
