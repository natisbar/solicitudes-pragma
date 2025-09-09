package co.com.pragma.r2dbc.mapper;

import co.com.pragma.model.solicitud.SolicitudPrestamo;
import co.com.pragma.model.solicitud.TipoPrestamo;
import co.com.pragma.r2dbc.model.dto.SolicitudPrestamoDto;
import org.springframework.stereotype.Component;

import static co.com.pragma.model.solicitud.enums.Estado.obtenerPorDescripcion;

@Component
public class SolicitudPrestamoMapper {

    public SolicitudPrestamo convertirDesde(SolicitudPrestamoDto dto){
        if (dto == null) return null;
        return SolicitudPrestamo.builder()
                .id(dto.getIdsolicitud())
                .monto(dto.getMonto())
                .plazo(dto.getPlazo())
                .correo(dto.getEmail())
                .tipoPrestamoId(dto.getIdtipoprestamo())
                .tipoPrestamo(TipoPrestamo.builder()
                        .id(dto.getIdtipoprestamo())
                        .nombre(dto.getTipoprestamo())
                        .tasaInteres(dto.getTasainteres())
                        .validacionAutomatica(dto.getValidacionautomatica())
                        .build())
                .estadoId(dto.getIdestado())
                .estado(obtenerPorDescripcion(dto.getEstado()))
                .build();
    }

}
