package org.example;

import controller.UserController;
import database.BaseDatabase;
import database.PostgresDatabase;
import io.javalin.Javalin;
import repository.UserRepository;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        try{
            BaseDatabase db = new PostgresDatabase();

            UserRepository userRepo = new UserRepository(db);
            UserController userController = new UserController(userRepo);

            // Configure Javalin with CORS plugin enabled
            Javalin app = Javalin.create(config -> {
                config.bundledPlugins.enableCors(cors -> {
                    cors.addRule(it -> {
                        // Replace with your actual frontend URL (e.g., Vite is usually 5173, Create React App is 3000 or 3001)
                        it.allowHost("http://localhost:5173", "http://localhost:3001");
                        it.allowCredentials = true;
                    });
                });
            }).start(3000);



            app.post("/api/signup", userController::registerUser);
            app.post("/api/signin", userController::loginUser);

            System.out.println("Javalin API server started successfully at http://localhost:3000/api/users");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
