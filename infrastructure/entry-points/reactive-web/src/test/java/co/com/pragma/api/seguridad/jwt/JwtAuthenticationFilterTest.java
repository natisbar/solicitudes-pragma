//package co.com.pragma.api.seguridad.jwt;
//
//import co.com.pragma.api.seguridad.TestSecurityConfig2;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.core.context.ReactiveSecurityContextHolder;
//import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.web.reactive.server.WebTestClient;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//import reactor.core.publisher.Mono;
//
//import java.nio.charset.StandardCharsets;
//import java.security.Key;
//import java.util.Date;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@WebFluxTest(controllers = JwtAuthenticationFilterTest.DummyController.class)
//@Import({JwtAuthenticationFilter.class, JwtService.class, TestSecurityConfig2.class})
//class JwtAuthenticationFilterTest {
//
//    @Autowired
//    private WebTestClient webTestClient;
//
//    @Autowired
//    private JwtService jwtService;
//
//    private String secretKey = "miSuperClaveSecretaDePrueba1234567890"; // min 32 chars
//    private Key key;
//
//    @BeforeEach
//    void setUp() {
//        // inyectar secretKey en el JwtService
//        org.springframework.test.util.ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
//        key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
//    }
//
//    private String generarToken(String subject, String rol) {
//        return Jwts.builder()
//                .setSubject(subject)
//                .claim("rol", rol)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + 60_000)) // 1 min
//                .signWith(key)
//                .compact();
//    }
//
//    @RestController
//    static class DummyController {
//        @GetMapping("/protegido")
//        public Mono<String> protegido() {
//            return ReactiveSecurityContextHolder.getContext()
//                    .map(ctx -> "Usuario: " + ctx.getAuthentication().getName());
//        }
//    }
//
//    @Test
//    void requestConTokenValido_debeAutenticar() {
//        String token = generarToken("usuario@test.com", "ADMIN");
//
//        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
//                .get().uri("/protegido")
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(String.class)
//                .value(body -> assertThat(body).contains("usuario@test.com"));
//    }
//
//    @Test
//    void requestConTokenInvalido_debeDevolver401() {
//        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
//                .get().uri("/protegido")
//                .header(HttpHeaders.AUTHORIZATION, "Bearer tokenFake123")
//                .exchange()
//                .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED);
//    }
//
//    @Test
//    void requestSinAuthorizationHeader_debePasarSinAutenticacion() {
//        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
//                .get().uri("/protegido")
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(String.class)
//                .value(body -> assertThat(body).isEqualTo("Usuario: null"));
//    }
//
//}
