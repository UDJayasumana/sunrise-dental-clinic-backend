package repository;

import classes.User;
import database.BaseDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserRepository {
    private final BaseDatabase database;
    public UserRepository(BaseDatabase database) {
        this.database = database;
    }

    public void post(User user) {
        String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
        try(Connection conn = database.getConnection(); // Uses the instance method
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.executeUpdate();
            System.out.println("Inserted successfully!");
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
