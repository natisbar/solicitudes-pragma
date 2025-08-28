package co.com.pragma.api;

import co.com.pragma.api.dto.FiltroPrestamoDto;
import co.com.pragma.api.dto.PaginacionDataDto;
import co.com.pragma.api.dto.PrestamoRespuestaDto;
import co.com.pragma.api.dto.PrestamoSolicitudDto;
import co.com.pragma.api.mapper.FiltroSolicitudMapper;
import co.com.pragma.api.mapper.SolicitudMapper;
import co.com.pragma.api.validador.ValidacionManejador;
import co.com.pragma.usecase.generarsolicitud.GenerarSolicitudUseCase;
import co.com.pragma.usecase.generarsolicitud.ObtenerSolicitudUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SolicitudHandler {

    private final ValidacionManejador validacionManejador;
    private final GenerarSolicitudUseCase generarSolicitudUseCase;
    private final ObtenerSolicitudUseCase obtenerSolicitudUseCase;
    private final FiltroSolicitudMapper filtroSolicitudMapper;
    private final SolicitudMapper solicitudMapper;

    public Mono<ServerResponse> listenPOSTUseCase(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(PrestamoSolicitudDto.class)
                .flatMap(validacionManejador::validar)
                .map(solicitudMapper::convertirDesde)
                .flatMap(generarSolicitudUseCase::ejecutar)
                .map(solicitudMapper::convertirA)
                .flatMap(dto -> ServerResponse.status(HttpStatus.CREATED).bodyValue(dto));
    }

    public Mono<ServerResponse> listenGETUseCase(ServerRequest serverRequest) {
        String tipoPrestamoId = serverRequest.queryParam("tipoPrestamoId").orElse(null);
        String correo = serverRequest.queryParam("correo").orElse(null);
        String pagina = serverRequest.queryParam("pagina").orElse(null);
        String tamano = serverRequest.queryParam("tamano").orElse(null);
        return Mono.just(new FiltroPrestamoDto(pagina, tamano, correo, tipoPrestamoId))
                .flatMap(validacionManejador::validar)
                .map(filtroSolicitudMapper::convertirDesde)
                .flatMap(obtenerSolicitudUseCase::obtenerPorSolicitudesPendientes)
                .map(paginacion -> PaginacionDataDto.<PrestamoRespuestaDto>builder()
                        .datos(paginacion.getDatos().stream()
                                .map(solicitudMapper::convertirA)
                                .toList())
                        .totalElementos(paginacion.getTotalElementos())
                        .totalPaginas(paginacion.getTotalPaginas())
                        .build())
                .flatMap(listaDto -> ServerResponse.ok().bodyValue(listaDto));
    }


}
