package co.com.pragma.model.solicitud;

import lombok.*;

import java.math.BigDecimal;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CuotaPago {
    private int numeroCuota;
    private BigDecimal cuota;
    private BigDecimal capital;
    private BigDecimal interes;
    private BigDecimal saldoRestante;
}
