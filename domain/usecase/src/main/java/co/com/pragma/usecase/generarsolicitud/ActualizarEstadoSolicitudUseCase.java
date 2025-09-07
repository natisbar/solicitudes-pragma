package co.com.pragma.usecase.generarsolicitud;

import co.com.pragma.model.solicitud.SolicitudPrestamo;
import co.com.pragma.model.solicitud.common.ex.NegocioException;
import co.com.pragma.model.solicitud.enums.Estado;
import co.com.pragma.model.solicitud.gateways.SolicitudPrestamoGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class ActualizarEstadoSolicitudUseCase {
    private final SolicitudPrestamoGateway solicitudPrestamoGateway;
    private static final String ESTADO_NO_ES_PARA_FINALIZAR = "El estado no es válido, debe ser RECHAZADO o APROBADO";
    private static final String SOLICITUD_NO_EXISTE = "La solicitud a actualizar no existe";

    public Mono<String> ejecutar(SolicitudPrestamo solicitudPrestamo) {
        return Mono.just(solicitudPrestamo)
                .flatMap(solicitud -> {
                    List<Estado> estadosFinalizados = Estado.obtenerEstadosFinalizados();
                    if (estadosFinalizados.contains(solicitud.getEstado())){
                        return solicitudPrestamoGateway.obtenerPorId(solicitud.getId())
                                .flatMap(solicitudEncontrada ->
                                        solicitudPrestamoGateway.actualizarEstado(solicitudPrestamo)
                                                .thenReturn("Solicitud actualizada correctamente"))
                                .switchIfEmpty(Mono.error(new NegocioException(SOLICITUD_NO_EXISTE)));
                    }
                    return Mono.error(new NegocioException(ESTADO_NO_ES_PARA_FINALIZAR));
                });
    }
}
