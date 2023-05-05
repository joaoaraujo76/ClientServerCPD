package server.services;

import server.repository.UsersRepository;
import server.models.User;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

public class UserAuthenticator implements Authenticator {

    public static boolean login(String username, String password) {
        Optional<User> user = UsersRepository.getUserByName(username);

        return user.filter(value -> {
            try {
                return (hashPassword(password).equals(value.getHashedPassword()));
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }).isPresent();
    }

    public static boolean register(String username, String password) throws IOException, NoSuchAlgorithmException {
        return UsersRepository.addUser(new User(username, hashPassword(password)));
    }

    private static String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hashedBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static boolean validToken(User user, String token) throws IOException{
        if (user.getToken().equals(token) && user.getExpiryDateToken() > System.currentTimeMillis()) {
            return true;
        } else {
            UsersRepository.invalidateTokenByUsername(user.getUsername());
            return false;
        }
    }
}