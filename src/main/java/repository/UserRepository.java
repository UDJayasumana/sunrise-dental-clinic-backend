package repository;

import classes.User;
import database.BaseDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class UserRepository {
    private final BaseDatabase database;
    private final Connection connection;

    private static final Logger logger = Logger.getLogger(UserRepository.class.getName());

    public UserRepository(BaseDatabase database) throws SQLException {
        this.database = database;
        connection = database.getConnection();
    }

    public void post(User user) throws Exception {

        String cleanedEmail = user.getEmail().trim().toLowerCase();
        user.setEmail(cleanedEmail);

        existsByEmail(user.getEmail());

        String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.executeUpdate();
            System.out.println("Inserted successfully!");
        }
        catch (Exception e) {
          //  logger.info("Received excep: " + e.getMessage());
            throw new IllegalArgumentException("email already exists in the system.");
        }

    }


    public boolean existsByEmail(String email) throws SQLException
    {
        String sql = "SELECT 1 FROM users WHERE email = ? LIMIT 1";

        try(PreparedStatement pstmt = connection.prepareStatement(sql))
        {
            //Set the email parameter
            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                // If rs.next() is true, it means a row with this email was found
                return rs.next();
            }
        }

    }

}
