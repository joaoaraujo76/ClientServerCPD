package client.states;

import protocol.Message;
import protocol.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class WaitingGameState implements ClientState {
    private final String token;
    private final Scanner scanner;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;

    public WaitingGameState(String token, Scanner scanner, ObjectInputStream input, ObjectOutputStream output) {
        this.token = token;
        this.scanner = scanner;
        this.input = input;
        this.output = output;
    }

    @Override
    public ClientState execute() {
        System.out.println("WAITING FOR GAME\n");
        try {
            Message message = (Message) input.readObject();

            switch (message.getType()) {
                case START_GAME -> {
                    System.out.println(message.getMessage());
                    return new GameState(token, scanner, input, output);
                }

                case ERROR -> {
                    System.out.println(message.getMessage());
                    return new MainMenuState(token, scanner, input, output);
                }

                default -> {
                    System.out.println(message.getMessage());
                    return this;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
