package controller;


import classes.User;
import exception.SunriseException;
import io.javalin.http.Context;
import io.javalin.http.Cookie;
import org.mindrot.jbcrypt.BCrypt;
import repository.UserRepository;
import utility.JwtUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class UserController {
    private final UserRepository userRepository;

    private static final Logger logger = Logger.getLogger(UserController.class.getName());

    public UserController(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }


    public void registerUser(Context ctx) {
        try {
            User incomingUser = ctx.bodyAsClass(User.class);

            //validate user
            incomingUser.validate();

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

            //Find user in the database
            User existingUser = userRepository.findByEmail(loginAttempt.getEmail());

            if(existingUser == null){
                //System.out.println("DEBUG: User not found in database!");
                ctx.status(401).json("{\"error\": \"Invalid email.\"}");
                return;
            }

            //Safely compare the plain-text login attempt with the stored BCrypt hash
            boolean passwordMatches = BCrypt.checkpw(
                    loginAttempt.getPassword(), // The raw password the user typed right now
                    existingUser.getPassword()    // The 60-character hash stored in your database
            );
            logger.info("Password match state: " + passwordMatches);
            if(!passwordMatches){
                ctx.status(401).json("{\"error\": \"Invalid password.\"}");
                return;
            }

            //Generate JWT Token
            String token = JwtUtil.generateToken(existingUser.getEmail());

            // 1. Create the cookie object using name and value
            Cookie myCookie = new Cookie("srdTK", token);

            // 2. Set the additional attributes using its setter methods
            myCookie.setMaxAge(86400);
            myCookie.setPath("/");
            myCookie.setHttpOnly(true);
            myCookie.setSecure(false);

            //Pass the Cookie object to the context
            ctx.cookie(myCookie);

            ctx.status(200).json("{\"message\": \"Login successful\"}");

        }catch (IllegalArgumentException e) {
            ctx.status(400).json("{\"error\": \"" + e.getMessage() + "\"}");
        }catch (Exception e) {
            ctx.status(500).json("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }



//    public void put(Context ctx) {
//        try {
//            String payload = ctx.body();
//           // userRepository.put(payload);
//            ctx.status(200).json("{\"message\": \"Resource updated successfully\"}");
//        } catch (Exception e) {
//            ctx.status(500).json("{\"error\": \"" + e.getMessage() + "\"}");
//        }
//    }

//    public void patch(Context ctx) {
//        try {
//            String payload = ctx.body();
//            //userRepository.patch(payload);
//            ctx.status(200).json("{\"message\": \"Resource patched successfully\"}");
//        } catch (Exception e) {
//            ctx.status(500).json("{\"error\": \"" + e.getMessage() + "\"}");
//        }
//    }

//    public void delete(Context ctx) {
//        try {
//            String idParam = ctx.pathParam("id");
//
//            Object id;
//            try {
//                id = Integer.parseInt(idParam);
//            } catch (NumberFormatException e) {
//                id = idParam;
//            }
//
//            //userRepository.delete(id);
//            ctx.status(200).json("{\"message\": \"Resource deleted successfully for ID: " + id + "\"}");
//        } catch (Exception e) {
//            ctx.status(500).json("{\"error\": \"" + e.getMessage() + "\"}");
//        }
//    }

}
