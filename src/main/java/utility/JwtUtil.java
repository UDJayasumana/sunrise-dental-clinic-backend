package utility;



import io.javalin.http.Cookie;
import io.javalin.http.SameSite;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

public class JwtUtil {
    // Keep this secret safe and secure (at least 256 bits)
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor("VIKMA_1RTH_5874_SDRT_1DET_HERE_SECURE_KEY".getBytes(StandardCharsets.UTF_8));
    // 30 minutes in milliseconds (30 * 60 * 1000)
    private static final long EXPIRATION_TIME = 1800000;

    public static String generateAccessToken(long userId)
    {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public static String generateRefreshToken()
    {
        String refreshToken = UUID.randomUUID().toString();
        return refreshToken;
    }

    public static Cookie getCookie( String tokenType, String tokenName, String token){

        int maxAge = "access".equals(tokenType) ? 60 * 60 : 3 * 24 * 60 * 60;

        Cookie cookie = new Cookie(tokenName, token);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setSameSite(SameSite.STRICT);

        return cookie;
    }


    public static String extractUserId(String token) {
        // Parse the JWT using your SECRET_KEY
        Claims claims = Jwts.parser()
                .verifyWith(SECRET_KEY) // In JJWT 0.12+, verifyWith() replaces setSigningKey()
                .build()
                .parseSignedClaims(token)
                .getPayload(); // getPayload() replaces getBody() in JJWT 0.12+

        // Since you used .subject(String.valueOf(userId)) when building it,
        // getSubject() will return the userId string.
        return claims.getSubject();
    }

}
