package classes;

import io.javalin.http.Context;
import io.jsonwebtoken.ExpiredJwtException;
import utility.JwtUtil;

import java.util.Map;

public class AuthGuard {

    // Returns true if valid, or sends 401 response and returns false if invalid
    public static boolean verifyToken(Context ctx) {
        String accessToken = ctx.cookie("srdAT");

        if (accessToken == null || accessToken.isEmpty()) {
            ctx.status(401).json(Map.of(
                    "statusCode", 401,
                    "message", "Validation failed",
                    "errors", Map.of("token", "Access token not found.")
            ));
            return false;
        }

        try {
            String userId = JwtUtil.extractUserId(accessToken);
            ctx.attribute("userId", userId); // Attach user ID
            return true;
        } catch (ExpiredJwtException e) {
            ctx.status(401).json(Map.of(
                    "statusCode", 401,
                    "message", "Token expired",
                    "errors", Map.of("token", "Access token has expired.")
            ));
            return false;
        } catch (Exception e) {
            ctx.status(401).json(Map.of(
                    "statusCode", 401,
                    "message", "Invalid token",
                    "errors", Map.of("token", "Invalid authentication token.")
            ));
            return false;
        }
    }
}