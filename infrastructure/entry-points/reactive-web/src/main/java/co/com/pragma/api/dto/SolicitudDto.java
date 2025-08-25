package co.com.pragma.api.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

import static co.com.pragma.api.common.Constantes.PATRON_CORREO;

public record SolicitudDto (
        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
        BigDecimal monto,
        @NotNull(message = "El plazo es obligatorio")
        @Min(value = 1, message = "El plazo(meses) debe ser mayor a 0")
        Integer plazo,
        @NotBlank(message = "El email es obligatorio y no puede estar vacio")
        @Pattern(regexp = PATRON_CORREO, message = "El formato del correo electronico no es correcto")
        String email,
        Long estadoId,
        String estadoDescripcion,
        @NotNull(message = "El tipoPrestamoId es obligatorio")
        Long tipoPrestamoId){}
