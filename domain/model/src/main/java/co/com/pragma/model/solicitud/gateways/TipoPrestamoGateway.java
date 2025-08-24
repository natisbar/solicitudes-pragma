package co.com.pragma.model.solicitud.gateways;

import co.com.pragma.model.solicitud.TipoPrestamo;
import reactor.core.publisher.Mono;

public interface TipoPrestamoGateway {
    Mono<TipoPrestamo> encontrarPorId(Long id);
    Mono<Boolean> existePorId(Long id);
}
