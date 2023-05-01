package server.repository;

import server.models.User;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UsersRepository implements Repository<User> {
    private static final Set<User> userList = new HashSet<>();

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

    public static boolean addUser(User user) throws IOException {
        if (UsersRepository.getUserList().add(user)) {
            try (FileWriter fileWriter = new FileWriter("data/users.txt", true)) {
                fileWriter.write(user.getUsername() + "," + user.getHashedPassword() + "," + user.getToken() + "," + user.getExpiryDateToken() + "\n");
            }
            return true;
        }
        return false;
    }
}
