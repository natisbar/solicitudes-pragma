package co.com.pragma.r2dbc.model.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SolicitudPrestamoDto {
    private Long idsolicitud;
    private BigDecimal monto;
    private Integer plazo;
    private String email;
    private Long idestado;
    private String estado;
    private Long idtipoprestamo;
    private String tipoprestamo;
    private Boolean validacionautomatica;
    private BigDecimal tasainteres;
}
