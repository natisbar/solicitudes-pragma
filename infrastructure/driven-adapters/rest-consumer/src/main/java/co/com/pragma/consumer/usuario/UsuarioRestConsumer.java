package co.com.pragma.consumer.usuario;

import co.com.pragma.model.solicitud.Usuario;
import co.com.pragma.model.solicitud.common.ex.IndisponibilidadException;
import co.com.pragma.model.solicitud.common.ex.NegocioException;
import co.com.pragma.model.solicitud.gateways.UsuarioGateway;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioRestConsumer implements UsuarioGateway{

    private final WebClient client;
    public static final String ERROR_CONSUMO_SERVICIO_REST_USUARIO = "Hay indisponibilidad en la api de usuarios. Por favor comuniquese con un administrador del sistema.";
    private static final Logger logger = LoggerFactory.getLogger(UsuarioRestConsumer.class);

    @Override
    @CircuitBreaker(name = "testPost" /*, fallbackMethod = "testGetOk"*/)
    public Flux<Usuario> obtenerPorListaCorreos(List<String> correos) {
        return client
                .post()
                .body(Mono.just(correos), new ParameterizedTypeReference<>() {
                })
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new NegocioException(body)))
                )
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new IndisponibilidadException(ERROR_CONSUMO_SERVICIO_REST_USUARIO + ": " + body)))
                )
                .bodyToMono(new ParameterizedTypeReference<List<Usuario>>() {})
                .flatMapMany(Flux::fromIterable)
                .onErrorResume(throwable -> {
                    if (!(throwable instanceof NegocioException)) logger.error(throwable.getMessage());
                    if (throwable instanceof WebClientRequestException) return Flux.error(new IndisponibilidadException(ERROR_CONSUMO_SERVICIO_REST_USUARIO));
                    return Flux.error(throwable);
                });
    }

}
