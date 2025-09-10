package co.com.pragma.sqs.listener.mapper;

import co.com.pragma.model.solicitud.SolicitudPrestamo;
import co.com.pragma.sqs.listener.dto.PrestamoSolicitudActualizarDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PrestamoSolicitudMapperTest {

    private PrestamoSolicitudMapper mapper = new PrestamoSolicitudMapper();

    @Test
    void debeConvertirDtoCorrectamente() {
        PrestamoSolicitudActualizarDto dto = new PrestamoSolicitudActualizarDto();
        dto.setId(123);
        dto.setEstado("APROBADO");
        String token = "abc.def.ghi";

        SolicitudPrestamo result = mapper.convertirDesde(dto, token);

        assertNotNull(result);
        assertEquals(123L, result.getId());
        assertEquals(4L, result.getEstadoId());
        assertEquals("APROBADO", result.getEstado().getValor());
        assertEquals("Bearer abc.def.ghi", result.getDataUsuario());
    }

    @Test
    void debeRetornarNullCuandoDtoEsNull() {
        SolicitudPrestamo result = mapper.convertirDesde(null, "token");
        assertNull(result);
    }

}
