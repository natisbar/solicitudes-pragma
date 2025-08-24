package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.SolicitudDto;
import co.com.pragma.model.solicitud.SolicitudPrestamo;
import co.com.pragma.model.solicitud.enums.Estado;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static co.com.pragma.model.solicitud.enums.Estado.obtenerPorId;

@Component
public class SolicitudMapper {
    public SolicitudPrestamo convertirDesde(SolicitudDto dto) {
        return Optional.ofNullable(dto)
                .map(solicitudDto -> SolicitudPrestamo.builder()
                        .monto(solicitudDto.monto())
                        .plazo(solicitudDto.plazo())
                        .email(solicitudDto.email())
                        .tipoPrestamoId(solicitudDto.tipoPrestamoId())
                        .estadoId(Estado.PENDIENTE.getId())
                        .build())
                .orElse(null);
    }

    public SolicitudDto convertirA(SolicitudPrestamo model) {
        return Optional.ofNullable(model)
                .map(solicitudPrestamo -> new SolicitudDto(
                        solicitudPrestamo.getMonto(),
                        solicitudPrestamo.getPlazo(),
                        solicitudPrestamo.getEmail(),
                        solicitudPrestamo.getEstadoId(),
                        obtenerPorId(solicitudPrestamo.getEstadoId()) != null ?
                                obtenerPorId(solicitudPrestamo.getEstadoId()).getValor() : null,
                        solicitudPrestamo.getTipoPrestamoId()))
                .orElse(null);
    }
}
