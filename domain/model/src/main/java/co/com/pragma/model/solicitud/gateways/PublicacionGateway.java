package co.com.pragma.model.solicitud.gateways;

import reactor.core.publisher.Mono;

public interface PublicacionGateway<T> {
    Mono<String> publicar(T mensaje);
}
