package server.commands;

import protocol.Message;
import protocol.MessageType;
import server.services.UserAuthenticator;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;

public class RegisterCommand implements Command {
    private final Message message;
    private final ObjectOutputStream output;

    public RegisterCommand(Message message, ObjectOutputStream output) {
        this.message = message;
        this.output = output;
    }

    @Override
    public void execute() throws IOException, NoSuchAlgorithmException {
        String username = message.getUsername();
        String password = message.getPassword();
        String token = message.getToken();

        System.out.println("Register -----------");
        System.out.println("Username: " + username);

        if (UserAuthenticator.register(username, password)) {
            System.out.println("Registration succeeded");
            output.writeObject(new Message(MessageType.REGISTERED, token, "Registration successful."));
            output.flush();
        } else {
            System.out.println("Registration failed");
            output.writeObject(new Message(MessageType.ERROR, token, "Registration failed, Username already exists."));
            output.flush();
        }
    }
}
