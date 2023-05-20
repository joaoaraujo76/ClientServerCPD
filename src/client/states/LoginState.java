package client.states;

import protocol.Message;
import protocol.MessageType;

import java.io.*;
import java.net.SocketTimeoutException;
import java.util.Scanner;

import static client.TokenUtils.updateToken;

public class LoginState implements ClientState {
    private final String token;
    private final Scanner scanner;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;
    private static final String REGISTER = "r";
    private static final String LOGIN = "l";

    public LoginState(String token, Scanner scanner, ObjectInputStream input, ObjectOutputStream output) {
        this.token = token;
        this.scanner = scanner;
        this.input = input;
        this.output = output;
    }

    @Override
    public ClientState execute() {
        System.out.print("\nDo you want to register or login? (r/l) ");
        String option = scanner.nextLine();

        switch (option) {
            case REGISTER -> {
                System.out.println("Register -----------");
                System.out.print("Username: ");
                String username = scanner.nextLine();

                System.out.print("Password: ");
                String password = scanner.nextLine();

                try {
                    output.writeObject(new Message(MessageType.REGISTER, token, username, password));
                    output.flush();

                    Message message = (Message) input.readObject();

                    switch (message.getType()) {

                        case TIMEOUT -> {
                            System.out.println(message.getMessage());
                            throw new SocketTimeoutException();
                        }

                        default -> {
                            System.out.println(message.getMessage());
                            return this;
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    // TODO: handle exceptions
                }
            }

            case LOGIN -> {
                System.out.println("Login -----------");
                System.out.print("Username: ");
                String username = scanner.nextLine();

                System.out.print("Password: ");
                String password = scanner.nextLine();

                try {
                    output.writeObject(new Message(MessageType.LOGIN, token, username, password));
                    output.flush();

                    Message authResult = (Message) input.readObject();

                    switch (authResult.getType()) {
                        case AUTHENTICATED -> {
                            System.out.println("Authentication succeeded.");
                            String newToken = authResult.getToken();
                            updateToken(newToken);
                            return new MainMenuState(newToken, scanner, input, output);
                        }

                        case RESUME_QUEUE -> {
                            System.out.println("Authentication succeeded. Returning to queue");
                            String newToken = authResult.getToken();
                            updateToken(newToken);
                            return new WaitingGameState(newToken, scanner, input, output);
                        }

                        case RESUME_GAME -> {
                            System.out.println("Authentication succeeded. Returning to game");
                            String newToken = authResult.getToken();
                            updateToken(newToken);
                            return new GameState(newToken, scanner, input, output);
                        }

                        case TIMEOUT -> {
                            System.out.println(authResult.getMessage());
                            throw new SocketTimeoutException();
                        }

                        default -> {
                            System.out.println(authResult.getMessage());
                            return this;
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    // TODO: handle exceptions
                }
            }

            default -> {
                System.out.println("Invalid option. Please try again.");
                return this;
            }
        }
        return null;
    }
}
