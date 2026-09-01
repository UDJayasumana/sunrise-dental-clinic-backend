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

            Javalin app = Javalin.create().start(3000);

            app.post("/api/register", userController::registerUser);
            app.post("/api/login", userController::loginUser);

            System.out.println("Javalin API server started successfully at http://localhost:3000/api/users");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
