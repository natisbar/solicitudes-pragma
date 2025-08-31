package co.com.pragma.model.solicitud.common.ex;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NegocioExceptionTest {
    @Test
    void constructorDebeAsignarMensajeCorrectamente() {
        // Arrange
        String mensajeEsperado = "Error de negocio";

        // Act
        NegocioException exception = new NegocioException(mensajeEsperado);

        // Assert
        assertThat(exception).isInstanceOf(NegocioException.class);
        assertThat(exception.getMessage()).isEqualTo(mensajeEsperado);
    }
}
