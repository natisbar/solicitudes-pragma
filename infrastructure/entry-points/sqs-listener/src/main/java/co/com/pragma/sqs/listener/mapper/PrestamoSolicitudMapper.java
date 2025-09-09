package co.com.pragma.sqs.listener.mapper;

import co.com.pragma.model.solicitud.SolicitudPrestamo;
import co.com.pragma.sqs.listener.dto.PrestamoSolicitudActualizarDto;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static co.com.pragma.model.solicitud.enums.Estado.obtenerPorDescripcion;

@Component
public class PrestamoSolicitudMapper {
    public SolicitudPrestamo convertirDesde(PrestamoSolicitudActualizarDto dto, String token) {
        return Optional.ofNullable(dto)
                .map(prestamoSolicitudDto -> SolicitudPrestamo.builder()
                        .id(Long.valueOf(prestamoSolicitudDto.getId()))
                        .estado(obtenerPorDescripcion(prestamoSolicitudDto.getEstado()))
                        .estadoId(obtenerPorDescripcion(prestamoSolicitudDto.getEstado()).getId())
                        .dataUsuario("Bearer ".concat(token))
                        .build())
                .orElse(null);
    }

}
