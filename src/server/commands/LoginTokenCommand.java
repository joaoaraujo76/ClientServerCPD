package server.commands;

import protocol.Message;
import protocol.MessageType;
import server.models.Player;
import server.models.User;
import server.repository.UsersRepository;
import server.authenticator.UserAuthenticator;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Optional;

import static server.repository.UsersRepository.updatePlayer;

public class LoginTokenCommand implements Command {
    private final Message message;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;
    private final Socket socket;

    public LoginTokenCommand(Message message, ObjectInputStream input, ObjectOutputStream output, Socket socket) {
        this.message = message;
        this.input = input;
        this.output = output;
        this.socket = socket;
    }

    @Override
    public void execute() {
        String token = message.getToken();

        Optional<User> userOptional = UsersRepository.getUserByToken(token);

        try {
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                if (UserAuthenticator.validToken(user, token)) {
                    System.out.println("Client authenticated: " + user.getUsername() + "\n");

                    switch (user.getState()) {
                        case QUEUE -> {
                            Player updatedPlayer = new Player(user, socket, input, output);
                            updatePlayer(updatedPlayer);

                            output.writeObject(new Message(MessageType.RESUME_QUEUE, token, "Token login successful. You are already queued into a game"));
                            output.flush();
                        }

                        case GAME -> {
                            output.writeObject(new Message(MessageType.RESUME_GAME, token, "Token login successful. Resuming you into the game"));
                            output.flush();
                        }

                        default -> {
                            output.writeObject(new Message(MessageType.AUTHENTICATED_TOKEN, token, "Token login successful."));
                            output.flush();
                        }
                    }

                } else {
                    System.out.println("Invalid token.");
                    output.writeObject(new Message(MessageType.ERROR, token, "Invalid token."));
                    output.flush();
                }
            } else {
                System.out.println("No user for given token.");
                output.writeObject(new Message(MessageType.ERROR, token, "No user for given token."));
                output.flush();
            }
        } catch (IOException e) {
            // TODO: handle exceptions;
        }
    }
}
