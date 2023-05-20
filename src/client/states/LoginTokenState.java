package client.states;

import protocol.Message;
import protocol.MessageType;

import java.io.*;
import java.net.SocketTimeoutException;
import java.util.Scanner;

import static client.TokenUtils.getClientToken;

public class LoginTokenState implements ClientState{
    private final Scanner scanner;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;

    public LoginTokenState(Scanner scanner, ObjectInputStream input, ObjectOutputStream output) {
        this.scanner = scanner;
        this.input = input;
        this.output = output;
    }

    @Override
    public ClientState execute() {
        try {
            System.out.println("Welcome!");

            String token = getClientToken();

            output.writeObject(new Message(MessageType.LOGIN_TOKEN, token));
            output.flush();

            Message message = (Message) input.readObject();

            switch (message.getType()) {
                case AUTHENTICATED_TOKEN -> {
                    System.out.println("Token Authentication succeeded.");
                    return new MainMenuState(token, scanner, input, output);
                }

                case RESUME_QUEUE -> {
                    System.out.println("Token Authentication succeeded. Returning to queue");
                    return new WaitingGameState(token, scanner, input, output);
                }

                case RESUME_GAME -> {
                    System.out.println("Token Authentication succeeded. Returning to game");
                    return new GameState(token, scanner, input, output);
                }

                case TIMEOUT -> {
                    System.out.println(message.getMessage());
                    throw new SocketTimeoutException();
                }

                default -> {
                    System.out.println(message.getMessage());
                    return new LoginState(token, scanner, input, output);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            // TODO: handle exceptions
        }
        return null;
    }
}
