package co.com.pragma.model.solicitud;
import co.com.pragma.model.solicitud.enums.Estado;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SolicitudPrestamo {
    private Long id;
    private BigDecimal monto;
    private Integer plazo;
    private String correo;
    private Long estadoId;
    private Long tipoPrestamoId;
    private Estado estado;
    private TipoPrestamo tipoPrestamo;
}
