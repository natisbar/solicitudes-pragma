package co.com.pragma.model.solicitud.gateways;

import co.com.pragma.model.solicitud.SolicitudPrestamo;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SolicitudPrestamoGateway {
    Mono<SolicitudPrestamo> guardar(SolicitudPrestamo solicitudPrestamo);
    Mono<Boolean> encontrarPorEmailYTipoPrestamoIdSinFinalizar(String correo, Long tipoSolicitudId, List<Long> estados);
}
