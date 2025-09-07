package co.com.pragma.api;

import co.com.pragma.api.dto.*;
import co.com.pragma.api.mapper.FiltroSolicitudMapper;
import co.com.pragma.api.mapper.SolicitudMapper;
import co.com.pragma.api.validador.ValidacionManejador;
import co.com.pragma.usecase.generarsolicitud.ActualizarEstadoSolicitudUseCase;
import co.com.pragma.usecase.generarsolicitud.GenerarSolicitudUseCase;
import co.com.pragma.usecase.generarsolicitud.ObtenerSolicitudUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
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
    private final ActualizarEstadoSolicitudUseCase actualizarEstadoSolicitudUseCase;
    private final FiltroSolicitudMapper filtroSolicitudMapper;
    private final SolicitudMapper solicitudMapper;

    public Mono<ServerResponse> listenPOSTUseCase(ServerRequest serverRequest) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .cast(String.class)
                .flatMap(correo -> serverRequest.bodyToMono(PrestamoSolicitudDto.class)
                        .flatMap(validacionManejador::validar)
                        .map(prestamoSolicitudDto ->
                                solicitudMapper.convertirDesde(prestamoSolicitudDto, correo))
                        .flatMap(generarSolicitudUseCase::ejecutar)
                        .map(solicitudMapper::convertirA)
                        .flatMap(dto -> ServerResponse.status(HttpStatus.CREATED).bodyValue(dto)));
    }

    public Mono<ServerResponse> listenPUTUseCase(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(PrestamoSolicitudActualizarDto.class)
                .flatMap(validacionManejador::validar)
                .map(solicitudMapper::convertirDesde)
                .flatMap(actualizarEstadoSolicitudUseCase::ejecutar)
                .flatMap(respuesta -> ServerResponse.status(HttpStatus.CREATED).bodyValue(respuesta));
    }

    public Mono<ServerResponse> listenGETUseCase(ServerRequest serverRequest) {
        String token = serverRequest.headers().firstHeader(HttpHeaders.AUTHORIZATION);
        String tipoPrestamoId = serverRequest.queryParam("tipoPrestamoId").orElse(null);
        String correo = serverRequest.queryParam("correo").orElse(null);
        String pagina = serverRequest.queryParam("pagina").orElse(null);
        String tamano = serverRequest.queryParam("tamano").orElse(null);
        return Mono.just(new FiltroPrestamoDto(pagina, tamano, correo, tipoPrestamoId))
                .flatMap(validacionManejador::validar)
                .map(filtroDto -> filtroSolicitudMapper.convertirDesde(filtroDto, token))
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
