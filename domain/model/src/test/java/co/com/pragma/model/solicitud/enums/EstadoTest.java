package co.com.pragma.model.solicitud.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EstadoTest {

    @Test
    void debeObtenerEstadoPorId() {
        assertEquals(Estado.PENDIENTE, Estado.obtenerPorId(1));
        assertEquals(Estado.RECHAZADA, Estado.obtenerPorId(2));
        assertEquals(Estado.REVISION, Estado.obtenerPorId(3));
        assertEquals(Estado.APROBADO, Estado.obtenerPorId(4));
        assertNull(Estado.obtenerPorId(99)); // no existe
    }

    @Test
    void debeObtenerEstadoPorDescripcion() {
        assertEquals(Estado.PENDIENTE, Estado.obtenerPorDescripcion("PENDIENTE REVISION"));
        assertEquals(Estado.RECHAZADA, Estado.obtenerPorDescripcion("RECHAZADA"));
        assertEquals(Estado.REVISION, Estado.obtenerPorDescripcion("REVISION MANUAL"));
        assertEquals(Estado.APROBADO, Estado.obtenerPorDescripcion("APROBADO"));
        assertNull(Estado.obtenerPorDescripcion("DESCONOCIDO")); // no existe
    }

    @Test
    void debeObtenerEstadosFinalizados() {
        List<Estado> finalizados = Estado.obtenerEstadosFinalizados();

        assertThat(finalizados)
                .containsExactlyInAnyOrder(Estado.RECHAZADA, Estado.APROBADO);

        assertTrue(finalizados.stream().allMatch(Estado::isEsFinalizado));
    }

    @Test
    void debeObtenerEstadosSinFinalizar() {
        List<Estado> sinFinalizar = Estado.obtenerEstadosSinFinalizar();

        assertThat(sinFinalizar)
                .containsExactlyInAnyOrder(Estado.PENDIENTE, Estado.REVISION);

        assertTrue(sinFinalizar.stream().noneMatch(Estado::isEsFinalizado));
    }
}
