package co.com.pragma.r2dbc.entities;

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
@Table(schema= "solicitudes", name = "tipo_prestamo")
public class TipoPrestamoData {
    @Id
    @Column("id_tipo_prestamo")
    private Long id;
    @Column("nombre")
    private String nombre;
    @Column("monto_minimo")
    private BigDecimal montoMinimo;
    @Column("monto_maximo")
    private BigDecimal montoMaximo;
    @Column("tasa_interes")
    private BigDecimal tasaInteres;
    @Column("validacion_automatica")
    private Boolean validacionAutomatica;
}
