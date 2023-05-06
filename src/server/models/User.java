package server.models;

import java.util.Objects;

public class User {
    private final String username;
    private String hashedPassword;
    private String token;
    private Long expiryDateToken;
    private Integer elo;
    private UserState state;
    private Long queueJoinTime;

    public User(String username, String hashedPassword, String token, Long expiryDateToken, Integer elo, String state, Long queueJoinTime) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.token = token;
        this.expiryDateToken = expiryDateToken;
        this.elo = elo;
        this.state = UserState.valueOf(state);
        this.queueJoinTime = queueJoinTime;
    }

    public User(String username, String hashedPassword) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.token = null;
        this.expiryDateToken = -1L;
        this.elo = 1000;
        this.state = UserState.NONE;
        this.queueJoinTime = -1L;
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

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }

    public Long getQueueJoinTime() {
        return queueJoinTime;
    }

    public void setQueueJoinTime(Long queueJoinTime) {
        this.queueJoinTime = queueJoinTime;
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
