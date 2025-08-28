package co.com.pragma.consumer.usuario;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UsuarioDto {
    private Long id;
    private String nombres;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private String identificacion;
    private String direccion;
    private String telefono;
    private String correoElectronico;
    private BigDecimal salarioBase;
}
