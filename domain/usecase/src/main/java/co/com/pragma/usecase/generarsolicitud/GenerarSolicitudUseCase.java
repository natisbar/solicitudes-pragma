package co.com.pragma.usecase.generarsolicitud;

import co.com.pragma.model.solicitud.Solicitud;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GenerarSolicitudUseCase {

    public Mono<Solicitud> ejecutar(Solicitud solicitud) {
        return Mono.just(solicitud);
    }
}
