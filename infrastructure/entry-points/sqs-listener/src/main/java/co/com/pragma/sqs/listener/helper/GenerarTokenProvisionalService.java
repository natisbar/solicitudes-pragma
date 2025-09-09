package co.com.pragma.sqs.listener.helper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class GenerarTokenProvisionalService {
    @Value("${security.jwt.secretkey}")
    private String secretKey;
    @Value("${security.user.admin}")
    private String emailAdmin;
    private static final long MILISEGUNDOS_VIGENCIA = 120000;

    public String ejecutar() {
        return Jwts.builder()
                .setSubject(emailAdmin)
                .claim("rol", "ADMIN")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + MILISEGUNDOS_VIGENCIA))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }
}
