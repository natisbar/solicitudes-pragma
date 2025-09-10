package co.com.pragma.sqs.listener.helper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class GenerarTokenProvisionalTest {
    private GenerarTokenProvisionalService service;
    private final String secretKey = "12345678901234567890123456789012";
    private final String emailAdmin = "admin@example.com";

    @BeforeEach
    void setUp() {
        service = new GenerarTokenProvisionalService();

        injectValue(service, "secretKey", secretKey);
        injectValue(service, "emailAdmin", emailAdmin);
    }

    @Test
    void debeGenerarTokenConSubjectYRolCorrectos() {
        String token = service.ejecutar();
        assertNotNull(token, "El token no debe ser nulo");

        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals(emailAdmin, claims.getSubject(), "El subject debe ser el email admin");
        assertEquals("ADMIN", claims.get("rol"), "El rol debe ser ADMIN");

        Date issuedAt = claims.getIssuedAt();
        Date expiration = claims.getExpiration();

        assertNotNull(issuedAt);
        assertNotNull(expiration);
        assertTrue(expiration.after(issuedAt), "La expiración debe ser posterior a issuedAt");
        assertTrue((expiration.getTime() - issuedAt.getTime()) <= 120000, "La vigencia debe ser de 2 minutos o menos");
    }

    private void injectValue(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
