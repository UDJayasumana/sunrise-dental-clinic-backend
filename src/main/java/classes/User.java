package classes;

import exception.SunriseException;

public class User {



    private long id;
    private String name;
    private String email;
    private String password;


    public long getId() {return id;}
    public void setId(long id) {this.id = id;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password;}


    public void validate(boolean isSignUpValidation) throws SunriseException {
        // 1. Name Validation
        if(isSignUpValidation) {
            if (name == null || name.trim().isEmpty()) {
                throw new SunriseException(400, "name", "Name cannot be null or empty", "Validation failed");
            }
        }

        // 2. Email Validation
            if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new SunriseException(400, "email", "A valid email address is required.", "Validation failed");
            }


        // 3. Password Validation Rules

            if (password == null || password.trim().isEmpty()) {
                throw new SunriseException(400, "password", "Password cannot be null or empty", "Validation failed");
            }
            if(isSignUpValidation) {
                if (password.length() < 8) {
                    throw new SunriseException(400, "password", "Password must be at least 8 characters long.", "Validation failed");
                }

                if (!password.matches(".*[A-Z].*")) {
                    throw new SunriseException(400, "password", "Password must contain at least one uppercase letter.", "Validation failed");
                }

                if (!password.matches(".*[a-z].*")) {
                    throw new SunriseException(400, "password", "Password must contain at least one lowercase letter.", "Validation failed");
                }

                if (!password.matches(".*\\d.*")) {
                    throw new SunriseException(400, "password", "Password must contain at least one number.", "Validation failed");
                }

                if (!password.matches(".*[@$!%*?&].*")) {
                    throw new SunriseException(400, "password", "Password must contain at least one special character (e.g., @$!%*?&).", "Validation failed");
                }
            }


    }

    @Override
    public String toString(){
        return "User { "     +
                "id: "       + id       + ", " +
                "name: "     + name     + ", " +
                "email: "    + email    + ", " +
                "password: " + password;
    }
}
