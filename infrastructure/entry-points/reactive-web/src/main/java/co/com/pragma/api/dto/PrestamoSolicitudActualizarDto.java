package co.com.pragma.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static co.com.pragma.api.common.Constantes.*;

@Schema(
        name = "PrestamoSolicitudDto",
        requiredProperties = {"monto", "plazo", "tipoPrestamoId"}
)
public record PrestamoSolicitudActualizarDto(
        @Schema(example = "1")
        @NotBlank(message = "El id de la solicitud es obligatorio")
        @Pattern(regexp = PATRON_ENTERO_POSITIVO, message = "El id debe ser un valor numerico mayor a 0")
        String id,

        @Schema(example = "1")
        @NotBlank(message = "El estado es obligatorio y no puede estar vacio")
        @Pattern(regexp = PATRON_ALFANUMERICO_ESPACIO, message = "El estado solo puede contener letras, espacios y números")
        String estado){}
