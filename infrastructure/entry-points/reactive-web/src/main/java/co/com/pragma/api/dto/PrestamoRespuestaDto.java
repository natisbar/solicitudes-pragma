package co.com.pragma.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PrestamoRespuestaDto(
        BigDecimal monto,
        Integer plazo,
        String email,
        Long estadoId,
        String estadoDescripcion,
        Long tipoPrestamoId,
        String tipoPrestamoDescripcion,
        String nombreSolicitante,
        BigDecimal salarioBaseSolicitante,
        BigDecimal tasaInteres,
        BigDecimal deudaTotalMensualSolicitudesAprobadas){}
