package server.repository;

import server.models.User;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

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
                fileWriter.write(user.getUsername() + "," + user.getHashedPassword() + "," + user.getToken() + "," + user.getExpiryDateToken());
            }
            return true;
        }
        return false;
    }

    public static void invalidateTokenByUsername(String username) {
        try {
            File file = new File("data/users.txt");
            Scanner scanner = new Scanner(file);

            List<String> updatedUsers = new ArrayList<>();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] fields = line.split(",");

                if (fields[0].equals(username)) {
                    fields[2] = null;
                    fields[3] = String.valueOf(-1);
                    line = String.join(",", fields);
                }
                updatedUsers.add(line);
            }

            scanner.close();

            FileWriter writer = new FileWriter(file);

            for (String line : updatedUsers) {
                writer.write(line + "\n");
            }

            writer.close();

            UsersRepository.getUserByName(username).ifPresent(u ->
                    u.setToken(null));
            UsersRepository.getUserByName(username).ifPresent(u ->
                    u.setExpiryDateToken(-1));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
