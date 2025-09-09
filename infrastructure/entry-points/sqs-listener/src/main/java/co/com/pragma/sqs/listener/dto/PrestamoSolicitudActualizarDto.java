package co.com.pragma.sqs.listener.dto;

import lombok.*;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PrestamoSolicitudActualizarDto {
        private long id;
        private String estado;
}
