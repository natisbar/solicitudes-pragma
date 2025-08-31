package co.com.pragma.model.solicitud.common.ex;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IndisponibilidadExceptionTest {
    @Test
    void constructorDebeAsignarMensajeCorrectamente() {
        // Arrange
        String mensajeEsperado = "Indisponible el servicio";

        // Act
        IndisponibilidadException exception = new IndisponibilidadException(mensajeEsperado);

        // Assert
        assertThat(exception).isInstanceOf(IndisponibilidadException.class);
        assertThat(exception.getMessage()).isEqualTo(mensajeEsperado);
    }
}
