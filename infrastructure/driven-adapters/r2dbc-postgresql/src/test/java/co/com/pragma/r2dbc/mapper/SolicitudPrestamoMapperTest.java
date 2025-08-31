package co.com.pragma.r2dbc.mapper;


import co.com.pragma.model.solicitud.SolicitudPrestamo;
import co.com.pragma.model.solicitud.enums.Estado;
import co.com.pragma.r2dbc.model.dto.SolicitudPrestamoDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SolicitudPrestamoMapperTest {

    SolicitudPrestamoMapper solicitudPrestamoMapper = new SolicitudPrestamoMapper();

    @Test
    void convertirDesdeDtoAModel_cuandoDtoExiste(){
        SolicitudPrestamoDto dto = SolicitudPrestamoDto.builder()
                .monto(BigDecimal.TEN)
                .email("email@email.com")
                .estado(Estado.APROBADO.getValor())
                .plazo(24)
                .idtipoprestamo(1L)
                .idsolicitud(1L)
                .build();

        SolicitudPrestamo respuesta = solicitudPrestamoMapper.convertirDesde(dto);

        assertEquals(dto.getMonto(), respuesta.getMonto());
        assertEquals(dto.getEmail(), respuesta.getCorreo());
    }

    @Test
    void convertirDesdeDtoAModel_cuandoDtoNull(){
        SolicitudPrestamoDto dto = null;

        SolicitudPrestamo respuesta = solicitudPrestamoMapper.convertirDesde(dto);

        assertNull(respuesta);
    }
}
