package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.PrestamoRespuestaDto;
import co.com.pragma.api.dto.PrestamoSolicitudDto;
import co.com.pragma.model.solicitud.SolicitudPrestamo;
import co.com.pragma.model.solicitud.enums.Estado;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

import static co.com.pragma.model.solicitud.enums.Estado.obtenerPorId;

@Component
public class SolicitudMapper {
    public SolicitudPrestamo convertirDesde(PrestamoSolicitudDto dto) {
        return Optional.ofNullable(dto)
                .map(prestamoSolicitudDto -> SolicitudPrestamo.builder()
                        .monto(new BigDecimal(prestamoSolicitudDto.monto()))
                        .plazo(Integer.parseInt(prestamoSolicitudDto.plazo()))
                        .correo(prestamoSolicitudDto.correo())
                        .tipoPrestamoId(Long.parseLong(prestamoSolicitudDto.tipoPrestamoId()))
                        .estadoId(Estado.PENDIENTE.getId())
                        .build())
                .orElse(null);
    }

    public PrestamoRespuestaDto convertirA(SolicitudPrestamo model) {
        return Optional.ofNullable(model)
                .map(solicitudPrestamo -> new PrestamoRespuestaDto(
                        solicitudPrestamo.getMonto(),
                        solicitudPrestamo.getPlazo(),
                        solicitudPrestamo.getCorreo(),
                        solicitudPrestamo.getEstadoId(),
                        obtenerPorId(solicitudPrestamo.getEstadoId()) != null ?
                                obtenerPorId(solicitudPrestamo.getEstadoId()).getValor() : null,
                        solicitudPrestamo.getTipoPrestamoId(),
                        solicitudPrestamo.getTipoPrestamo() != null ? solicitudPrestamo.getTipoPrestamo().getNombre() : null,
                        solicitudPrestamo.getSolicitante() != null ? solicitudPrestamo.getSolicitante().getNombres() : null,
                        solicitudPrestamo.getSolicitante() != null ? solicitudPrestamo.getSolicitante().getSalarioBase() : null,
                        solicitudPrestamo.getTipoPrestamo() != null ? solicitudPrestamo.getTipoPrestamo().getTasaInteres() : null,
                        null
                        )
                )
                .orElse(null);
    }
}
