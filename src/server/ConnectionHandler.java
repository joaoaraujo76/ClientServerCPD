package server;

import protocol.Message;
import protocol.MessageType;
import server.parser.UsersParser;
import server.services.UserAuthenticator;
import server.repository.UsersRepository;
import server.models.User;

import java.io.*;
import java.net.Socket;
import java.text.ParseException;
import java.util.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

public class ConnectionHandler implements Runnable {
    private final Socket clientSocket;

    public ConnectionHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
            output.flush();
            UsersParser.parse();

            while (true) {
                Object obj = input.readObject();

                if (obj instanceof Message message) {
                    String tokenClient = message.getToken();
                    System.out.println("Token received: " + tokenClient);

                    switch (message.getType()) {

                        case REGISTER -> {
                            String username = message.getUsername();
                            String password = message.getPassword();

                            System.out.println("Register -----------");
                            System.out.println("Username: " + username);

                            if (UserAuthenticator.register(username, password)) {
                                System.out.println("Registration succeeded");
                                output.writeObject(new Message(MessageType.REGISTERED, tokenClient, "Registration successful."));
                                output.flush();
                            } else {
                                System.out.println("Registration failed");
                                output.writeObject(new Message(MessageType.ERROR, tokenClient, "Registration failed, Username already exists."));
                                output.flush();
                            }
                        }

                        case LOGIN -> {
                            String username = message.getUsername();
                            String password = message.getPassword();

                            System.out.println("Login -----------");
                            System.out.println("Username: " + username);

                            if (UserAuthenticator.login(username, password)) {
                                String newToken = generateRandomToken();
                                updateTokenByUsername(username, newToken);
                                System.out.println("Authenticated succeeded\n");
                                output.writeObject(new Message(MessageType.AUTHENTICATED, tokenClient, "Login successful."));
                                output.flush();
                            } else {
                                System.out.println("Authenticated failed");
                                output.writeObject(new Message(MessageType.ERROR, tokenClient, "Login failed, Username or password incorrect."));
                                output.flush();
                            }
                        }

                        case LOGIN_TOKEN -> {
                            Optional<User> userOptional = UsersRepository.getUserByToken(tokenClient);
                            if (userOptional.isPresent()) {
                                User user = userOptional.get();
                                if (UserAuthenticator.validToken(user, tokenClient)) {
                                    System.out.println("Client authenticated: " + user.getUsername() + "\n");
                                    output.writeObject(new Message(MessageType.AUTHENTICATED_TOKEN, tokenClient, "Token login successful."));
                                } else {
                                    System.out.println("Invalid token.");
                                    output.writeObject(new Message(MessageType.ERROR, tokenClient, "Invalid token."));
                                }
                            } else {
                                System.out.println("No user for given token.");
                                output.writeObject(new Message(MessageType.ERROR, tokenClient, "No user for given token."));
                            }
                        }
                    }
                }
            }
        } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | ParseException e) {
            // TODO: handle exceptions;
        }
    }

    private static String generateRandomToken() {
        Random random = new Random();
        int randomInt = random.nextInt();
        return Integer.toHexString(randomInt);
    }

    private static void updateTokenByUsername(String username, String newToken) throws IOException {
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
