package co.com.pragma.api.seguridad.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
    private JwtService jwtService;
    private String secretKey = "miSuperClaveSecretaDePrueba1234567890";
    private Key key;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);

        key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void validarToken_conTokenValido_retornaClaims() {
        // Arrange: generar token válido
        String token = Jwts.builder()
                .setSubject("usuario@test.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60)) // 1 min
                .signWith(key)
                .compact();

        // Act
        Claims claims = jwtService.validarToken(token);

        // Assert
        assertNotNull(claims);
        assertEquals("usuario@test.com", claims.getSubject());
    }

    @Test
    void validarToken_conTokenInvalido_lanzaExcepcion() {
        String tokenInvalido = "eyJhbGciOiJIUzI1NiJ9.fake.payload";

        assertThrows(Exception.class, () -> jwtService.validarToken(tokenInvalido));
    }
}
