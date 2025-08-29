package co.com.pragma.api.exception;

import co.com.pragma.model.solicitud.common.ex.ConflictoException;
import co.com.pragma.model.solicitud.common.ex.IndisponibilidadException;
import co.com.pragma.model.solicitud.common.ex.NegocioException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@Component
public class ManejadorGlobalErrores extends AbstractErrorWebExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ManejadorGlobalErrores.class);
    private static final HashMap<Class<?>, HttpStatus> httpStatusCodes = new HashMap<>();

    public ManejadorGlobalErrores(ErrorAttributes errorAttributes, ApplicationContext applicationContext,
                                  ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, new WebProperties.Resources(), applicationContext);
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        httpStatusCodes.put(NegocioException.class, HttpStatus.BAD_REQUEST);
        httpStatusCodes.put(ConflictoException.class, HttpStatus.CONFLICT);
        httpStatusCodes.put(IndisponibilidadException.class, HttpStatus.SERVICE_UNAVAILABLE);
    }

    private Mono<ServerResponse> construirRespuestaError(ServerRequest request) {
        Throwable throwable = this.getError(request);
        HttpStatus responseCode = obtenerCodigoRespuesta((Exception) throwable);
        String message = throwable.getMessage();
        Error error = new Error(responseCode.value(), message);

        if (!(throwable instanceof NegocioException) && !Exceptions.isMultiple(throwable)
                && !(throwable instanceof WebExchangeBindException)
                && !(throwable instanceof ConstraintViolationException)
                && !(throwable instanceof IndisponibilidadException)) {
            responseCode = throwable instanceof ResponseStatusException responseStatusException ?
                    HttpStatus.valueOf(responseStatusException.getStatusCode().value()) :
                    HttpStatus.INTERNAL_SERVER_ERROR;
            error = new Error();

            logger.error(message, throwable);
        }
        return ServerResponse.status(responseCode)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(error);
    }

    private HttpStatus obtenerCodigoRespuesta(Exception throwable) {
        HttpStatus statusCode = httpStatusCodes.get(throwable.getClass());
        return statusCode == null ? HttpStatus.BAD_REQUEST : statusCode;
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::construirRespuestaError);
    }
}
