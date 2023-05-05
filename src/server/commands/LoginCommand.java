package server.commands;

import protocol.Message;
import protocol.MessageType;
import server.repository.UsersRepository;
import server.services.UserAuthenticator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class LoginCommand implements Command {
    private final Message message;
    private final ObjectOutputStream output;

    public LoginCommand(Message message, ObjectOutputStream output) {
        this.message = message;
        this.output = output;
    }

    @Override
    public void execute() throws IOException {
        String username = message.getUsername();
        String password = message.getPassword();
        String token = message.getToken();

        System.out.println("Login -----------");
        System.out.println("Username: " + username);

        if (UserAuthenticator.login(username, password)) {
            String newToken = generateRandomToken();
            updateTokenByUsername(username, newToken);
            System.out.println("Authenticated succeeded\n");
            output.writeObject(new Message(MessageType.AUTHENTICATED, newToken, "Login successful."));
            output.flush();
        } else {
            System.out.println("Authenticated failed");
            output.writeObject(new Message(MessageType.ERROR, token, "Login failed, Username or password incorrect."));
            output.flush();
        }
    }

    private static String generateRandomToken() {
        Random random = new Random();
        int randomInt = random.nextInt();
        return Integer.toHexString(randomInt);
    }

    private static void updateTokenByUsername(String username, String newToken) {
        try {
            long nowInMillis = System.currentTimeMillis();
            long sevenDaysInMillis = 7 * 24 * 60 * 60 * 1000L;
            long sevenDaysFromNowInMillis = nowInMillis + sevenDaysInMillis;

            File file = new File("data/users.txt");
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
    }
}
