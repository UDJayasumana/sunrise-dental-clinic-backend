package repository;

import classes.Appointment;
import classes.User;
import database.BaseDatabase;
import org.mindrot.jbcrypt.BCrypt;

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

        //Hash the password using BCrypt securely
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        //logger.info("hashed pass: " + hashedPassword);

        String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, hashedPassword);
            stmt.executeUpdate();
            System.out.println("Inserted successfully!");
        }
        catch (Exception e) {
          //  logger.info("Received excep: " + e.getMessage());
            throw new IllegalArgumentException("email already exists in the system.");
        }

    }

    public User getById(long userId) throws Exception{
        String sql = "SELECT id, name, email FROM users WHERE id = ?";

        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            // Set the ID parameter
            pstmt.setLong(1, userId);

            try (ResultSet rs = pstmt.executeQuery()){
                if (rs.next()) {
                    User user = new User();

                    // Map database columns to your Appointment object setters
                    user.setId(rs.getLong("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));

                    return user;
                }
            }

        }catch (Exception e){
            throw new IllegalArgumentException("appointment saving failed");
        }
        return null;
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

    public User findByEmail(String email) throws SQLException {
        String cleanedEmail = email.trim().toLowerCase();
        String sql = "SELECT id, name, email, password FROM users WHERE email = ? LIMIT 1";


        try (PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setString(1, cleanedEmail);

            try (ResultSet rs = pstmt.executeQuery()) {
                //logger.info("Data: " + rs);
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    return user;
                }
            }

        }catch (SQLException e){
            throw new RuntimeException("Error finding user by email: " + e.getMessage(), e);
        }
        return null;

    }

}
