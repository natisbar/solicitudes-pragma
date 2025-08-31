package co.com.pragma.model.solicitud.common.ex;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConflictoExceptionTest {

    @Test
    void constructorDebeAsignarMensajeCorrectamente() {
        // Arrange
        String mensajeEsperado = "Ya existe un conflicto en la solicitud";

        // Act
        ConflictoException exception = new ConflictoException(mensajeEsperado);

        // Assert
        assertThat(exception).isInstanceOf(NegocioException.class);
        assertThat(exception.getMessage()).isEqualTo(mensajeEsperado);
    }
}
