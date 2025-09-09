package co.com.pragma.model.solicitud.gateways;

import reactor.core.publisher.Mono;

public interface NotificacionGateway<T> {
    Mono<String> iniciarValidacion(T mensaje);
    Mono<String> responder(T mensaje);
}
