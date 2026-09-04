package repository;

import classes.RefreshToken;
import classes.User;
import database.BaseDatabase;
import io.javalin.http.NotFoundResponse;
import utility.JwtUtil;

import java.io.FileNotFoundException;
import java.io.InvalidObjectException;
import java.sql.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.MissingResourceException;
import java.util.UUID;

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

    public RefreshToken refreshTokens(UUID token) throws Exception
    {
        String sql = "SELECT id, token, user_Id, expiry_date, revoked FROM refreshtokens WHERE token = ?";

        try(PreparedStatement pstmt = connection.prepareStatement(sql)){

            pstmt.setObject(1, token);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {

                    RefreshToken refreshToken = new RefreshToken();

                    refreshToken.setId(rs.getLong("id"));
                    refreshToken.setToken((UUID) rs.getObject("token"));
                    refreshToken.setUserId(rs.getString("user_Id"));
                    refreshToken.setExpiryDate(Timestamp.valueOf(rs.getString("expiry_date")));
                    refreshToken.setRevoked(rs.getBoolean("revoked"));

                    System.out.println("RT Token: " + refreshToken.getToken());
                    System.out.println("RT ExpiryDate: " + refreshToken.getExpiryDate());
                    System.out.println("RT UserID: " + refreshToken.getUserId());

                    if(JwtUtil.isValidUUID(refreshToken.getToken().toString()))
                    {
                        if (!(refreshToken.getExpiryDate().before(new Date()) || refreshToken.getExpiryDate().equals(new Date()))){
                            String tempRefreshToken = JwtUtil.generateRefreshToken();

                            Date expiryDate = Date.from(Instant.now().plus(3, ChronoUnit.DAYS));
                            Timestamp expiryDateTimestamp = new Timestamp(expiryDate.getTime());

                            deleteByUserId(refreshToken.getUserId());
                            RefreshToken newRefreshedToken = new RefreshToken(UUID.fromString(tempRefreshToken), String.valueOf(refreshToken.getUserId()), expiryDateTimestamp, false);
                            post(newRefreshedToken);

                            return newRefreshedToken;
                        }
                    }

                }


            }

        } catch (Exception e) {
          //  System.out.println("Error deleting tokens: " + e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }

        return null;
    }

}
