package co.com.pragma.sqs.listener;

import co.com.pragma.model.solicitud.SolicitudPrestamo;
import co.com.pragma.sqs.listener.dto.PrestamoSolicitudActualizarDto;
import co.com.pragma.sqs.listener.helper.GenerarTokenProvisionalService;
import co.com.pragma.sqs.listener.mapper.PrestamoSolicitudMapper;
import co.com.pragma.usecase.generarsolicitud.ActualizarEstadoSolicitudUseCase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class SQSProcessor implements Function<Message, Mono<Void>> {
    private final ActualizarEstadoSolicitudUseCase actualizarEstadoSolicitudUseCase;
    private final GenerarTokenProvisionalService generarTokenProvisionalService;
    private final PrestamoSolicitudMapper prestamoSolicitudMapper;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> apply(Message message) {
        System.out.println(message.body());
        return construirSolicitud(message.body())
                .flatMap(actualizarEstadoSolicitudUseCase::ejecutar)
                .then();
    }

    private Mono<SolicitudPrestamo> construirSolicitud(String body){
        return Mono.just(body)
                .map(solicitud -> convertirStringASolicitud(body))
                .map(prestamoSolicitudActualizarDto -> prestamoSolicitudMapper
                        .convertirDesde(prestamoSolicitudActualizarDto, generarTokenProvisionalService.ejecutar()));
    }

    private PrestamoSolicitudActualizarDto convertirStringASolicitud(String solicitudString){
        try {
            return objectMapper.readValue(solicitudString, PrestamoSolicitudActualizarDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
