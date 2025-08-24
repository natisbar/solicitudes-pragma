package co.com.pragma.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SolicitudDto (
        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
        BigDecimal monto,
        @NotNull(message = "El plazo es obligatorio")
        @Min(value = 1, message = "El plazo(meses) debe ser mayor a 0")
        Integer plazo,
        @NotBlank(message = "El email es obligatorio y no puede estar vacio")
        String email,
        Long estadoId,
        String estadoDescripcion,
        @NotNull(message = "El tipoPrestamoId es obligatorio")
        Long tipoPrestamoId){}
