package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.SolicitudDto;
import co.com.pragma.model.solicitud.Solicitud;
import co.com.pragma.model.solicitud.enums.Estado;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static co.com.pragma.model.solicitud.enums.Estado.obtenerPorId;

@Component
public class SolicitudMapper {
    public Solicitud convertirDesde(SolicitudDto dto) {
        return Optional.ofNullable(dto)
                .map(solicitudDto -> Solicitud.builder()
                        .monto(solicitudDto.monto())
                        .plazo(solicitudDto.plazo())
                        .email(solicitudDto.email())
                        .tipoPrestamoId(solicitudDto.tipoPrestamoId())
                        .estadoId(Estado.PENDIENTE.getId())
                        .build())
                .orElse(null);
    }

    public SolicitudDto convertirA(Solicitud model) {
        return Optional.ofNullable(model)
                .map(solicitud -> new SolicitudDto(
                        solicitud.getMonto(),
                        solicitud.getPlazo(),
                        solicitud.getEmail(),
                        solicitud.getEstadoId(),
                        obtenerPorId(solicitud.getEstadoId()) != null ?
                                obtenerPorId(solicitud.getEstadoId()).getValor() : null,
                        solicitud.getTipoPrestamoId()))
                .orElse(null);
    }
}
