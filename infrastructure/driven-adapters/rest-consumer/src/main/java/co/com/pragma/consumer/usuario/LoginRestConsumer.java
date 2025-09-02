package co.com.pragma.consumer.usuario;

import co.com.pragma.consumer.model.dto.LoginDto;
import co.com.pragma.model.solicitud.common.ex.IndisponibilidadException;
import co.com.pragma.model.solicitud.common.ex.NegocioException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

@Service
public class LoginRestConsumer{

    private final WebClient client;
    public static final String ERROR_CONSUMO_SERVICIO_REST_USUARIO = "Hay indisponibilidad en la api de usuarios. Por favor comuniquese con un administrador del sistema.";
    private static final Logger logger = LoggerFactory.getLogger(LoginRestConsumer.class);
    private final LoginDto loginDto;

    public LoginRestConsumer(@Qualifier("login") WebClient client,
                             @Value("${adapter.restconsumer-login.username}") String nombreUsuario,
                             @Value("${adapter.restconsumer-login.password}") String password) {
        this.client = client;
        this.loginDto = LoginDto.builder()
                .correoElectronico(nombreUsuario).contrasena(password)
                .build();
    }


    @CircuitBreaker(name = "testPost" /*, fallbackMethod = "testGetOk"*/)
    public Mono<String> obtenerToken() {
        return client
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginDto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new NegocioException(body)))
                )
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new IndisponibilidadException(ERROR_CONSUMO_SERVICIO_REST_USUARIO + ": " + body)))
                )
                .bodyToMono(String.class)
                .onErrorResume(throwable -> {
                    if (!(throwable instanceof NegocioException)) logger.error(throwable.getMessage());
                    if (throwable instanceof WebClientRequestException) return Mono.error(new IndisponibilidadException(ERROR_CONSUMO_SERVICIO_REST_USUARIO));
                    return Mono.error(throwable);
                });
    }



}
