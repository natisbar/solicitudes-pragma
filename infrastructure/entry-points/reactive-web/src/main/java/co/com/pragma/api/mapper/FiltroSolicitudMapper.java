package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.FiltroPrestamoDto;
import co.com.pragma.model.solicitud.FiltroData;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FiltroSolicitudMapper {
    public FiltroData convertirDesde(FiltroPrestamoDto dto) {
        return Optional.ofNullable(dto)
                .map(filtroPrestamo -> FiltroData.builder()
                        .pagina((Integer.parseInt(filtroPrestamo.pagina()) - 1) * Integer.parseInt(filtroPrestamo.tamano()))
                        .tamano(Integer.parseInt(filtroPrestamo.tamano()))
                        .correo(filtroPrestamo.correo())
                        .tipoPrestamoId(filtroPrestamo.tipoPrestamoId() != null ? Long.parseLong(filtroPrestamo.tipoPrestamoId())
                                : null)
                        .build())
                .orElse(null);
    }
}
