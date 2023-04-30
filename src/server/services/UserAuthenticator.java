package server.services;

import server.repository.UsersRepository;
import server.models.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

public class UserAuthenticator implements Authenticator{

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

    public static boolean register(String username, String password) throws NoSuchAlgorithmException {
        return UsersRepository.getUserList().add(new User(username, hashPassword(password), null, null));
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
}