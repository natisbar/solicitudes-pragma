package co.com.pragma.api.seguridad;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.security.config.web.server.SecurityWebFiltersOrder.AUTHENTICATION;

@TestConfiguration
@EnableWebFluxSecurity
public class TestSecurityConfig {

    /**
     * Reutiliza tu configuración de seguridad real pero con un filtro simulado
     * que inyecta un Authentication válido en el SecurityContext.
     */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/v1/solicitudes").hasRole("CLIENTE") // respeta tus reglas reales
                        .anyExchange().authenticated()
                )
                .addFilterAt((exchange, chain) -> {
                    // Simulamos un Authentication válido
                    Authentication auth = new UsernamePasswordAuthenticationToken(
                            "correo@corre.com", // 👉 simula el "sub" del JWT
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_CLIENTE"), new SimpleGrantedAuthority("ROLE_ASESOR")) // 👉 rol que necesites
                    );
                    SecurityContext context = new SecurityContextImpl(auth);

                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
                }, AUTHENTICATION)
                .build();
    }
}