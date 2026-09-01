package controller;


import classes.User;
import io.javalin.http.Context;
import repository.UserRepository;

public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }


    public void post(Context ctx) {
        try {
            User incomingUser = ctx.bodyAsClass(User.class);
            userRepository.post(incomingUser);
            ctx.status(201).json("{\"message\": \"Resource created successfully\"}");
        } catch (Exception e) {
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
