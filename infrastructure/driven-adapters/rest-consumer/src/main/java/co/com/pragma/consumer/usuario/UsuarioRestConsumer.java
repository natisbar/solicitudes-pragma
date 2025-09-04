package co.com.pragma.consumer.usuario;

import co.com.pragma.model.solicitud.Usuario;
import co.com.pragma.model.solicitud.common.ex.IndisponibilidadException;
import co.com.pragma.model.solicitud.common.ex.NegocioException;
import co.com.pragma.model.solicitud.gateways.UsuarioGateway;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class UsuarioRestConsumer implements UsuarioGateway{

    private final WebClient client;
    public static final String ERROR_CONSUMO_SERVICIO_REST_USUARIO = "Hay indisponibilidad en la api de usuarios. Por favor comuniquese con un administrador del sistema.";
    public static final String TOKEN_INVALIDO = "Token inválido o expirado";
    private static final Logger logger = LoggerFactory.getLogger(UsuarioRestConsumer.class);

    public UsuarioRestConsumer(WebClient client) {
        this.client = client;
    }

    @Override
    @CircuitBreaker(name = "testPost" /*, fallbackMethod = "testGetOk"*/)
    public Flux<Usuario> obtenerPorListaCorreos(List<String> correos, String token) {
        return client.post()
                .uri("/v1/usuarios/por-correos")
                .header(HttpHeaders.AUTHORIZATION, token)
                .bodyValue(correos)
                .exchangeToMono(response -> {
                    HttpStatusCode status = response.statusCode();

                    if (status.is2xxSuccessful()) {
                        return response.bodyToMono(new ParameterizedTypeReference<List<Usuario>>() {});
                    }
                    if (status.is4xxClientError() && status.value() != 401) {
                        return response.bodyToMono(String.class)
                                .defaultIfEmpty("Solicitud inválida")
                                .flatMap(body -> Mono.error(new NegocioException(body)));
                    }
                    if (status.value() == 401) {
                        return response.bodyToMono(String.class)
                                .doOnSuccess(mensaje -> logger.error(TOKEN_INVALIDO))
                                .defaultIfEmpty(ERROR_CONSUMO_SERVICIO_REST_USUARIO)
                                .flatMap(body -> Mono.error(new IndisponibilidadException(body)));
                    }
                    if (status.is5xxServerError()) {
                        return response.bodyToMono(String.class)
                                .doOnSuccess(mensaje -> logger.error(mensaje))
                                .defaultIfEmpty(ERROR_CONSUMO_SERVICIO_REST_USUARIO)
                                .flatMap(body ->
                                        Mono.error(new IndisponibilidadException(ERROR_CONSUMO_SERVICIO_REST_USUARIO)));
                    }
                    return response.createException().flatMap(Mono::error);
                })
                .flatMapMany(Flux::fromIterable);
    }

}
