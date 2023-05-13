package client.states;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class GameState implements ClientState {
    private final String token;
    private final Scanner scanner;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;

    public GameState(String token, Scanner scanner, ObjectInputStream input, ObjectOutputStream output) {
        this.token = token;
        this.scanner = scanner;
        this.input = input;
        this.output = output;
    }

    @Override
    public ClientState execute() {
        System.out.println("IN GAME\n");
        try {
            Thread.sleep(60 * 1000); // 1 min
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return this;
    }
}
