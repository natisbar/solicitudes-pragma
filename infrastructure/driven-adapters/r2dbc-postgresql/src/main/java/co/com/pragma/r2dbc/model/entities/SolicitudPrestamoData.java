package co.com.pragma.r2dbc.model.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(schema= "solicitudes", name = "solicitud")
public class SolicitudPrestamoData {
    @Id
    @Column("id_solicitud")
    private Long id;
    @Column("monto")
    private BigDecimal monto;
    @Column("plazo")
    private Integer plazo;
    @Column("email")
    private String correo;
    @Column("id_estado")
    private Long estadoId;
    @Column("id_tipo_prestamo")
    private Long tipoPrestamoId;
}
