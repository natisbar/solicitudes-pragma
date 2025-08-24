package co.com.pragma.api.validador;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Set;

@Component
public class ValidacionManejador {

    private final Validator validador;

    public ValidacionManejador(Validator validador) {
        this.validador = validador;
    }

    public <T> Mono<T> validar(T object) {
        return Mono.defer(() -> {
            Set<ConstraintViolation<T>> violations = validador.validate(object);
            if (!violations.isEmpty())
                return Mono.error(new ConstraintViolationException(violations));
            return Mono.just(object);
        });
    }
}