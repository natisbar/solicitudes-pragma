package co.com.pragma.api.seguridad;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                        .pathMatchers(HttpMethod.GET, "/v1/solicitudes").hasRole("ASESOR")
                        .pathMatchers(HttpMethod.PUT, "/v1/solicitudes").hasRole("ASESOR")
                        .pathMatchers(HttpMethod.POST, "/v1/solicitudes").hasRole("CLIENTE")
                        .anyExchange().authenticated()
                )
                .addFilterAt((exchange, chain) -> {

                    Map<String, Object> detalles = new HashMap<>();
                    detalles.put("salarioBase", 123.6);

                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            "correo@corre.com", // 👉 simula el "sub" del JWT
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_CLIENTE"), new SimpleGrantedAuthority("ROLE_ASESOR"))
                    );
                    auth.setDetails(detalles);
                    SecurityContext context = new SecurityContextImpl(auth);

                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
                }, AUTHENTICATION)
                .build();
    }
}