package client.states;

import protocol.Message;
import protocol.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class MainMenuState implements ClientState {
    private final String token;
    private final Scanner scanner;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;
    private static final String SIMPLE_QUEUE = "s";
    private static final String RANKED_QUEUE = "r";
    private static final String CHANGE_PASSWORD = "p";
    private static final String SEE_ELO = "e";


    public MainMenuState(String token, Scanner scanner, ObjectInputStream input, ObjectOutputStream output) {
        this.token = token;
        this.scanner = scanner;
        this.input = input;
        this.output = output;
    }

    @Override
    public ClientState execute() {

        System.out.println("\nMain Menu");
        System.out.println("-----------");
        System.out.println("Do you want to queue into simple or ranked play? (s/r) ");
        System.out.println("Do you want to change password? (p) ");
        System.out.println("Do you want to see your Elo? (e) ");

        String option = scanner.nextLine();

        switch (option.toLowerCase()) {
            case SIMPLE_QUEUE -> {
                try {
                    output.writeObject(new Message(MessageType.JOIN_SIMPLE_QUEUE, token));
                    output.flush();

                    System.out.println(((Message) input.readObject()).getMessage());
                } catch (IOException | ClassNotFoundException e) {
                    //TODO: handle exceptions
                }
                return new WaitingGameState(token, scanner, input, output);
            }
            case RANKED_QUEUE -> {
                try {
                    output.writeObject(new Message(MessageType.JOIN_RANKED_QUEUE, token));
                    output.flush();

                    System.out.println(((Message) input.readObject()).getMessage());
                } catch (IOException | ClassNotFoundException e) {
                    //TODO: handle exceptions
                }
                return new WaitingGameState(token, scanner, input, output);
            }
            case CHANGE_PASSWORD -> {
                System.out.println("Change password -----------");

                String password;
                String passwordValidator;

                while(true) {
                    System.out.print("New password: ");
                    password = scanner.nextLine();
                    System.out.print("Confirm new password: ");
                    passwordValidator = scanner.nextLine();

                    if(!password.equals(passwordValidator)) {
                        System.out.println("Passwords do not match");
                    } else break;
                }

                try {
                    output.writeObject(new Message(MessageType.CHANGE_PASSWORD, token, password));
                    output.flush();

                    System.out.println(((Message) input.readObject()).getMessage());
                } catch (IOException | ClassNotFoundException e) {
                    //TODO: handle exceptions
                }
                return this;
            }

            case SEE_ELO -> {

                try {
                    output.writeObject(new Message(MessageType.SEE_ELO, token));
                    output.flush();

                    System.out.println(((Message) input.readObject()).getMessage());
                } catch (IOException | ClassNotFoundException e) {
                    //TODO: handle exceptions
                }

                return this;
            }

            default -> {
                System.out.println("Invalid option. Please try again.");
                return this;
            }
        }
    }
}
