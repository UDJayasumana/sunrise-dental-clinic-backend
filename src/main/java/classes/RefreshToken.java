package classes;

import org.postgresql.util.PGTimestamp;


import java.sql.Timestamp;
import java.util.UUID;

public class RefreshToken {

    private long id;
    private UUID token;
    private String userId;
    private Timestamp expiryDate;
    private boolean revoked;

    public long getId() {return id;}
    public void setId(long id) {this.id = id;}

    public UUID getToken() {return token;}
    public void setToken(UUID token) {this.token = token;}

    public String getUserId() {return userId;}
    public void setUserId(String userId) {this.userId = userId;}

    public Timestamp getExpiryDate() {return expiryDate;}
    public void setExpiryDate(Timestamp expiryDate) {this.expiryDate = expiryDate;}

    public boolean isRevoked() {return revoked;}
    public void setRevoked(boolean revoked) {this.revoked = revoked;}

    public RefreshToken(UUID token, String userId, Timestamp expiryDate, boolean revoked)
    {
        this.token = token;
        this.userId = userId;
        this.expiryDate = expiryDate;
        this.revoked = revoked;
    }

}
