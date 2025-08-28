package co.com.pragma.consumer.usuario;

import co.com.pragma.model.solicitud.Usuario;
import co.com.pragma.model.solicitud.common.ex.NegocioException;
import co.com.pragma.model.solicitud.gateways.UsuarioGateway;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioRestConsumer implements UsuarioGateway{

    private final WebClient client;
    public static final String ERROR_CONSUMO_SERVICIO_REST_USUARIO = "Se presentó un error al consumidor rest de usuario";

    // these methods are an example that illustrates the implementation of WebClient.
    // You should use the methods that you implement from the Gateway from the domain.
//    @CircuitBreaker(name = "testGet" /*, fallbackMethod = "testGetOk"*/)
//    public Mono<List<UsuarioDto>> testGet() {
//        return client
//                .get()
//                .retrieve()
//                .bodyToMono(new ParameterizedTypeReference<List<UsuarioDto>>() {});
//    }

// Possible fallback method
//    public Mono<String> testGetOk(Exception ignored) {
//        return client
//                .get() // TODO: change for another endpoint or destination
//                .retrieve()
//                .bodyToMono(String.class);
//    }
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
                                .flatMap(body -> Mono.error(new RuntimeException(ERROR_CONSUMO_SERVICIO_REST_USUARIO)))
                )
                .bodyToMono(new ParameterizedTypeReference<List<Usuario>>() {})
                .flatMapMany(Flux::fromIterable);
    }

}
