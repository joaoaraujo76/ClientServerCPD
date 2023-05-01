package server.models;

import java.util.Objects;

public class User {
    private final String username;
    private String hashedPassword;
    private String token;
    private long expiryDateToken;

    public User(String username, String hashedPassword, String token, long expiryDateToken) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.token = token;
        this.expiryDateToken = expiryDateToken;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) { this.token = token; }

    public long getExpiryDateToken() {
        return expiryDateToken;
    }

    public void setExpiryDateToken(long expiryDateToken) {
        this.expiryDateToken = expiryDateToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
