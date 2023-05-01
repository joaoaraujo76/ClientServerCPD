package server.repository;

import server.models.User;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UsersRepository {
    private static Set<User> userList = new HashSet<>();

    public static Set<User> getUserList() {
        return userList;
    }

    public static Optional<User> getUserByName(String name) {
        for (User user : userList) {
            if (user.getUsername().equals(name)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public static Optional<User> getUserByToken(String token) {
        for (User user : userList) {
            if (user.getToken().equals(token)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}
