package co.com.pragma.model.solicitud.gateways;

import co.com.pragma.model.solicitud.SolicitudPrestamo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

public interface SolicitudPrestamoGateway {
    Mono<SolicitudPrestamo> guardar(SolicitudPrestamo solicitudPrestamo);
    Mono<Boolean> existePorEmailYTipoPrestamoIdSinFinalizar(String correo, Long tipoSolicitudId, List<Long> estados);
    Flux<SolicitudPrestamo> obtenerPorEstados(List<Long> estados, int pagina, int tamano);
    Flux<SolicitudPrestamo> obtenerPorEstadosYCorreoYTipoPrestamoId(List<Long> estados, String correo, Long tipoPrestamoId, int pagina, int tamano);
    Flux<SolicitudPrestamo> obtenerPorEstadosYCorreo(List<Long> estados, String correo, int pagina, int tamano);
    Flux<SolicitudPrestamo> obtenerPorEstadosYTipoPrestamoId(List<Long> estados, Long tipoPrestamoId, int pagina, int tamano);
    Mono<Long> contarPorEstados(List<Long> estados);
    Mono<Long> contarPorEstadosYCorreoYTipoPrestamoId(List<Long> estados, String correo, Long tipoPrestamoId);
    Mono<Long> contarPorEstadosYCorreo(List<Long> estados, String correo);
    Mono<Long> contarPorEstadosYTipoPrestamoId(List<Long> estados, Long tipoPrestamoId);
    Mono<BigDecimal> obtenerDeudaTotalMensualSolicitudesAprobadas(String correo);
}
