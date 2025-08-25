package co.com.pragma.config;

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
    @Import(UseCasesConfig.class) // importa tu config real que hace el @ComponentScan
    static class TestConfig {

        @Bean
        public co.com.pragma.model.solicitud.gateways.TipoPrestamoGateway tipoPrestamoGateway() {
            return mock(co.com.pragma.model.solicitud.gateways.TipoPrestamoGateway.class);
        }

        @Bean
        public co.com.pragma.model.solicitud.gateways.SolicitudPrestamoGateway solicitudPrestamoGateway() {
            return mock(co.com.pragma.model.solicitud.gateways.SolicitudPrestamoGateway.class);
        }
    }
}