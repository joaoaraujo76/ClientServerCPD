package server.data;

import server.models.UserState;
import server.models.User;
import server.repository.UsersRepository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UsersData {
    private static final String DATA_FILE = "src/server/data/users.txt";
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();

    public static boolean addUser(User user) throws IOException {
        lock.writeLock().lock();
        try {
            if (UsersRepository.getUserList().add(user)) {
                try (FileWriter fileWriter = new FileWriter(DATA_FILE, true)) {
                    fileWriter.write(
                            user.getUsername() + "," +
                                    user.getHashedPassword() + "," +
                                    user.getToken() + "," +
                                    user.getExpiryDateToken() + "," +
                                    user.getElo() + "," +
                                    user.getState() + "," +
                                    user.getQueueJoinTime() + "\n"
                    );
                }
                return true;
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }

    }

    public static void invalidateTokenByUsername(String username) {

        lock.writeLock().lock();
        try {
            try {
                File file = new File(DATA_FILE);
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
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void updateTokenByUsername(String username, String newToken) {
        lock.writeLock().lock();
        try {
            try {
                long nowInMillis = System.currentTimeMillis();
                long sevenDaysInMillis = 7 * 24 * 60 * 60 * 1000L;
                long sevenDaysFromNowInMillis = nowInMillis + sevenDaysInMillis;

                File file = new File(DATA_FILE);
                Scanner scanner = new Scanner(file);
                List<String> updatedUsers = new ArrayList<>();
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] fields = line.split(",");

                    if (fields[0].equals(username)) {
                        fields[2] = newToken;
                        fields[3] = String.valueOf(sevenDaysFromNowInMillis);
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
                        u.setToken(newToken));
                UsersRepository.getUserByName(username).ifPresent(u ->
                        u.setExpiryDateToken(sevenDaysFromNowInMillis));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            lock.writeLock().unlock();
        }

    }

    public static void updateStateByUsername(String username, Long time, UserState state) {
        lock.writeLock().lock();
        try {
            try {
                File file = new File(DATA_FILE);
                Scanner scanner = new Scanner(file);

                List<String> updatedUsers = new ArrayList<>();

                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] fields = line.split(",");

                    if (fields[0].equals(username)) {
                        fields[5] = String.valueOf(state);
                        fields[6] = String.valueOf(time);
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
                        u.setState(state));
                UsersRepository.getUserByName(username).ifPresent(u ->
                        u.setQueueJoinTime(time));

            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            lock.writeLock().unlock();
        }

    }

    public static void updateEloByUsername(String username, Integer elo) {

        lock.writeLock().lock();
        try {
            try {
                File file = new File(DATA_FILE);
                Scanner scanner = new Scanner(file);

                List<String> updatedUsers = new ArrayList<>();

                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] fields = line.split(",");

                    if (fields[0].equals(username)) {
                        fields[4] = String.valueOf(elo);
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
                        u.setElo(elo));

            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            lock.writeLock().unlock();
        }

    }

    public static void updatePasswordByUsername(String username, String password) {

        lock.writeLock().lock();
        try {
            try {
                File file = new File(DATA_FILE);
                Scanner scanner = new Scanner(file);

                List<String> updatedUsers = new ArrayList<>();

                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] fields = line.split(",");

                    if (fields[0].equals(username)) {
                        fields[1] = String.valueOf(password);
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
                        u.setHashedPassword(password));

            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            lock.writeLock().unlock();
        }

    }
}
