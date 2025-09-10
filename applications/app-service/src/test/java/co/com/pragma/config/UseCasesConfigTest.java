package co.com.pragma.config;

import co.com.pragma.model.solicitud.gateways.NotificacionGateway;
import co.com.pragma.model.solicitud.gateways.SolicitudPrestamoGateway;
import co.com.pragma.model.solicitud.gateways.TipoPrestamoGateway;
import co.com.pragma.model.solicitud.gateways.UsuarioGateway;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(TestConfig.class)) {

            String[] beanNames = context.getBeanDefinitionNames();

            boolean useCaseBeanFound = false;
            for (String beanName : beanNames) {
                if (beanName.endsWith("UseCase")) {
                    useCaseBeanFound = true;
                    break;
                }
            }

            assertTrue(useCaseBeanFound, "No beans ending with 'UseCase' were found");
        }
    }

    @Configuration
    @Import(UseCasesConfig.class)
    static class TestConfig<T> {

        @Bean
        public TipoPrestamoGateway tipoPrestamoGateway() {
            return mock(TipoPrestamoGateway.class);
        }

        @Bean
        public SolicitudPrestamoGateway solicitudPrestamoGateway() {
            return mock(SolicitudPrestamoGateway.class);
        }

        @Bean
        public UsuarioGateway usuarioGateway() {
            return mock(UsuarioGateway.class);
        }

        @Bean
        public NotificacionGateway<T> notificacionGateway() {
            return mock(NotificacionGateway.class);
        }
    }
}