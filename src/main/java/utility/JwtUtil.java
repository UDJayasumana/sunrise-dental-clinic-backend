package utility;



import io.jsonwebtoken.Jwts;
import java.security.Key;
import java.util.Date;

public class JwtUtil {
    // Keep this secret safe and secure (at least 256 bits)
    private static final Key SECRET_KEY = Jwts.SIG.HS256.key().build();
    private static final long EXPIRATION_TIME = 86400000; // 1 day in milliseconds

    public static String generateToken(String email)
    {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }
}
