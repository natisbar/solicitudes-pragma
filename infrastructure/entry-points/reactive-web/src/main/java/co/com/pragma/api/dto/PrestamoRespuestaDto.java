package co.com.pragma.api.dto;

import java.math.BigDecimal;

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
