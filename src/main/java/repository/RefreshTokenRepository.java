package repository;

import classes.RefreshToken;
import database.BaseDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class RefreshTokenRepository {
    private final BaseDatabase database;
    private final Connection connection;

    public  RefreshTokenRepository(BaseDatabase database) throws SQLException{
        this.database = database;
        connection = database.getConnection();
    }

    public void post(RefreshToken refreshToken) throws Exception{

        String sql = "INSERT INTO refreshtokens (token, user_id, expiry_date, revoked) VALUES (?, ?, ?, ?)";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setObject(1, refreshToken.getToken());
            stmt.setString(2, refreshToken.getUserId());
            stmt.setString(3, refreshToken.getExpiryDate().toString());
            stmt.setBoolean(4, refreshToken.isRevoked());
            stmt.executeUpdate();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new IllegalArgumentException("email already exists in the system.");
        }
    }

    public void deleteByUserId(String userId) throws Exception {
        String sql = "DELETE FROM refreshtokens WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error deleting tokens: " + e.getMessage());
            throw new Exception("Could not delete existing tokens for user.");
        }
    }

}
