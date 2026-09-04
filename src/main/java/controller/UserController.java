package controller;


import classes.Appointment;
import classes.AuthGuard;
import classes.RefreshToken;
import classes.User;
import database.BaseDatabase;
import exception.SunriseException;
import io.javalin.http.Context;
import io.javalin.http.Cookie;
import org.mindrot.jbcrypt.BCrypt;
import repository.RefreshTokenRepository;
import repository.UserRepository;
import utility.JwtUtil;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class UserController {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private static final Logger logger = Logger.getLogger(UserController.class.getName());

    public UserController(BaseDatabase baseDatabase) throws SQLException {
        this.userRepository = new UserRepository(baseDatabase);
        this.refreshTokenRepository = new RefreshTokenRepository(baseDatabase);
    }


    public void registerUser(Context ctx) {
        try {
            User incomingUser = ctx.bodyAsClass(User.class);

            //validate user
            incomingUser.validate(true);

            userRepository.post(incomingUser);


            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", 201);
            response.put("message", "REGISTER_SUCCESS");
            response.put("data", null); // This will render as `null` in JSON

            ctx.status(201).json(response);

        } catch (SunriseException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", e.getStatusCode());
            response.put("message", e.getMessage());
            response.put("errors", Map.of(e.getField(), e.getValue()));

            ctx.status(e.getStatusCode()).json(response);

        } catch (Exception e) {
            ctx.status(500).json("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    public void loginUser(Context ctx){
        try{
            User loginAttempt = ctx.bodyAsClass(User.class);

            loginAttempt.validate(false);

            //Find user in the database
            User existingUser = userRepository.findByEmail(loginAttempt.getEmail());
            System.out.println(existingUser);

            if(existingUser == null){
                //System.out.println("DEBUG: User not found in database!");
                //ctx.status(401).json("{\"error\": \"Invalid email.\"}");
                Map<String, Object> response = new HashMap<>();
                response.put("statusCode", 404);
                response.put("message", "Not Found");
                response.put("errors", Map.of("email", "Email not found."));

                ctx.status(404).json(response);
                return;
            }

            //Safely compare the plain-text login attempt with the stored BCrypt hash
            boolean passwordMatches = BCrypt.checkpw(
                    loginAttempt.getPassword(), // The raw password the user typed right now
                    existingUser.getPassword()    // The 60-character hash stored in your database
            );
            logger.info("Password match state: " + passwordMatches);
            if(!passwordMatches){
                //ctx.status(401).json("{\"error\": \"Invalid password.\"}");

                Map<String, Object> response = new HashMap<>();
                response.put("statusCode", 400);
                response.put("message", "Validation failed");
                response.put("errors", Map.of("password", "Invalid password."));

                ctx.status(400).json(response);
                return;
            }

            //Generate JWT Token
            String accessToken  = JwtUtil.generateAccessToken(existingUser.getId());
            String refreshToken = JwtUtil.generateRefreshToken();

            Cookie accessCookie = JwtUtil.getCookie("access", "srdAT", accessToken);
            Cookie refreshCookie = JwtUtil.getCookie("refresh", "srdRT", refreshToken);

            //Delete if available any previous refresh tokens
            refreshTokenRepository.deleteByUserId(String.valueOf(existingUser.getId()));

            Date expiryDate = Date.from(Instant.now().plus(3, ChronoUnit.DAYS));
            Timestamp expiryDateTimestamp = new Timestamp(expiryDate.getTime());
            RefreshToken refreshTokeen = new RefreshToken(UUID.fromString(refreshToken), String.valueOf(existingUser.getId()), expiryDateTimestamp, false);
            refreshTokenRepository.post(refreshTokeen);

            //Pass the Cookie object to the context
            ctx.cookie(accessCookie);
            ctx.cookie(refreshCookie);

            //Send the login success response
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", 200);
            response.put("message", "LOGIN_SUCCESS");
            response.put("data", null);
            ctx.status(200).json(response);

        }catch (SunriseException e) {
            //ctx.status(400).json("{\"error\": \"" + e.getMessage() + "\"}");
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", e.getStatusCode());
            response.put("message", e.getMessage());
            response.put("errors", Map.of(e.getField(), e.getValue()));

            ctx.status(e.getStatusCode()).json(response);
        }catch (Exception e) {
            ctx.status(500).json("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    public void logoutUser(Context ctx) throws Exception {
        try {
            String accessTokenValue = ctx.cookie("srdAT");

            if (accessTokenValue != null) {
                String userId = JwtUtil.extractUserId(accessTokenValue);
                System.out.println("UserID: " + userId);
                refreshTokenRepository.deleteByUserId(userId);
            }

            //Clear the cookies on the client browser
            ctx.removeCookie("srdAT");
            ctx.removeCookie("srdRT");

            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", 200);
            response.put("message", "LOGOUT_SUCCESS");
            response.put("data", null);

            ctx.status(200).json(response);
        }catch(Exception e){
            ctx.status(500).json("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    public void getUserById(Context ctx){
        try{
            long id = ctx.pathParamAsClass("id", Long.class).get();
            System.out.println(id);

            User user = userRepository.getById(id);

            if (user != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("statusCode", 200);
                response.put("message", "USER_FETCHED");
                response.put("data", user);

                ctx.status(200).json(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("statusCode", 404);
                response.put("message", "USER_NOT_FOUND");
                response.put("data", null);

                ctx.status(404).json(response);
            }

        }catch (SunriseException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", e.getStatusCode());
            response.put("message", e.getMessage());
            response.put("errors", Map.of(e.getField(), e.getValue()));

            ctx.status(e.getStatusCode()).json(response);
        } catch (Exception e) {
            ctx.status(500).json("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    public void verifyToken(Context ctx) {
        // Run the guard check; if it fails, it already sent the 401 response
        if (!AuthGuard.verifyToken(ctx)) {
            return;
        }

        // If valid, proceed with your controller logic
        String userId = ctx.attribute("userId");

        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", 200);
        response.put("message", "VERIFIED");
        response.put("data", Map.of("userId", userId));

        ctx.status(200).json(response);
    }

    public void refreshTokens(Context ctx)
    {
        try{
            String refreshToken = ctx.cookie("srdRT");

            RefreshToken reToken = null;

            if(refreshToken != null)
            {
                reToken = refreshTokenRepository.refreshTokens(UUID.fromString(refreshToken));
            }

            if(reToken != null)
            {
                Map<String, Object> response = new HashMap<>();
                response.put("statusCode", 200);
                response.put("message", "TOKENS_REFRESHED");
                response.put("data", null);
                ctx.status(200).json(response);
            }
            else
            {
                Map<String, Object> response = new HashMap<>();
                response.put("statusCode", 401);
                response.put("message", "REFRESH_TOKEN_EXPIRED");
                response.put("data", null);
                ctx.status(401).json(response);
            }

        }catch (SunriseException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", e.getStatusCode());
            response.put("message", e.getMessage());
            response.put("errors", Map.of(e.getField(), e.getValue()));

            ctx.status(e.getStatusCode()).json(response);
        }catch(Exception e){
            ctx.status(500).json("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

}
