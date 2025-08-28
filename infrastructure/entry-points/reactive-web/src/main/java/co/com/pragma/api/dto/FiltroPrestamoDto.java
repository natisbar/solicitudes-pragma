package co.com.pragma.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static co.com.pragma.api.common.Constantes.PATRON_CORREO;
import static co.com.pragma.api.common.Constantes.PATRON_ENTERO_POSITIVO;

public record FiltroPrestamoDto(
        @NotBlank(message = "La página es obligatoria")
        @Pattern(regexp = PATRON_ENTERO_POSITIVO, message = "La pagina es númerica y debe ser mayor a 0")
        String pagina,

        @NotBlank(message = "El tamaño es obligatorio")
        @Pattern(regexp = PATRON_ENTERO_POSITIVO, message = "El tamaño es numerico y debe ser mayor a 0")
        String tamano,

        @Pattern(regexp = PATRON_CORREO, message = "El formato del correo electronico no es correcto")
        String correo,

        @Pattern(regexp = PATRON_ENTERO_POSITIVO, message = "El id del tipo de prestamo es numerico y debe ser mayor a 0")
        String tipoPrestamoId){}


