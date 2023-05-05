package server.commands;

import protocol.Message;
import protocol.MessageType;
import server.models.User;
import server.repository.UsersRepository;
import server.services.UserAuthenticator;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Optional;

public class LoginTokenCommand implements Command {
    private final Message message;
    private final ObjectOutputStream output;

    public LoginTokenCommand(Message message, ObjectOutputStream output) {
        this.message = message;
        this.output = output;
    }

    @Override
    public void execute() throws IOException {
        String token = message.getToken();

        Optional<User> userOptional = UsersRepository.getUserByToken(token);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (UserAuthenticator.validToken(user, token)) {
                System.out.println("Client authenticated: " + user.getUsername() + "\n");
                output.writeObject(new Message(MessageType.AUTHENTICATED_TOKEN, token, "Token login successful."));
            } else {
                System.out.println("Invalid token.");
                output.writeObject(new Message(MessageType.ERROR, token, "Invalid token."));
            }
        } else {
            System.out.println("No user for given token.");
            output.writeObject(new Message(MessageType.ERROR, token, "No user for given token."));
        }
    }
}
