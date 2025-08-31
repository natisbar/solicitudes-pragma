package co.com.pragma.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static co.com.pragma.api.common.Constantes.*;

@Schema(
        name = "PrestamoSolicitudDto",
        requiredProperties = {"monto", "plazo", "tipoPrestamoId"}
)
public record PrestamoSolicitudDto(
        @Schema(example = "2000000")
        @NotBlank(message = "El monto es obligatorio y no puede estar vacio")
        @Pattern(regexp = PATRON_MONTO, message = "El monto debe ser un valor numerico mayor a 0")
        String monto,

        @Schema(example = "12")
        @NotBlank(message = "El plazo es obligatorio y no puede estar vacio")
        @Pattern(regexp = PATRON_ENTERO_POSITIVO, message = "El plazo(meses) es numerico y debe ser mayor a 0")
        String plazo,

        @Schema(example = "1")
        @NotBlank(message = "El tipo de prestamo es obligatorio y no puede estar vacio")
        @Pattern(regexp = PATRON_ENTERO_POSITIVO, message = "El id del tipo de prestamo es numerico y debe ser mayor a 0")
        String tipoPrestamoId){}
