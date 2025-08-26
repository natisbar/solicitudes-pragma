package co.com.pragma.api;

import co.com.pragma.api.dto.PrestamoSolicitudDto;
import co.com.pragma.api.mapper.SolicitudMapper;
import co.com.pragma.api.validador.ValidacionManejador;
import co.com.pragma.usecase.generarsolicitud.GenerarSolicitudUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final ValidacionManejador validacionManejador;
    private final GenerarSolicitudUseCase generarSolicitudUseCase;
    private final SolicitudMapper solicitudMapper;

    public Mono<ServerResponse> listenPOSTUseCase(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(PrestamoSolicitudDto.class)
                .flatMap(validacionManejador::validar)
                .map(solicitudMapper::convertirDesde)
                .flatMap(generarSolicitudUseCase::ejecutar)
                .map(solicitudMapper::convertirA)
                .flatMap(dto -> ServerResponse.status(HttpStatus.CREATED).bodyValue(dto));
    }
}
