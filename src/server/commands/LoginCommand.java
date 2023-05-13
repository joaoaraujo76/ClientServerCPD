package server.commands;

import protocol.Message;
import protocol.MessageType;
import server.authenticator.UserAuthenticator;
import server.models.User;
import server.models.UserState;
import server.repository.UsersRepository;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Optional;
import java.util.Random;

import static server.data.UsersData.updateTokenByUsername;

public class LoginCommand implements Command {
    private final Message message;
    private final ObjectOutputStream output;

    public LoginCommand(Message message, ObjectOutputStream output) {
        this.message = message;
        this.output = output;
    }

    @Override
    public void execute() {
        String username = message.getUsername();
        String password = message.getPassword();
        String token = message.getToken();

        System.out.println("Login -----------");
        System.out.println("Username: " + username);

        if (UserAuthenticator.login(username, password)) {
            String newToken = generateRandomToken();
            updateTokenByUsername(username, newToken);
            System.out.println("Authenticated succeeded\n");

            Optional<User> userOptional = UsersRepository.getUserByName(username);
            userOptional.ifPresent(user -> {
                try {
                    switch (user.getState()) {
                        case QUEUE -> {
                            output.writeObject(new Message(MessageType.RESUME_QUEUE, newToken, "Login successful. You are already queued into a game"));
                            output.flush();
                        }

                        case GAME -> {
                            output.writeObject(new Message(MessageType.RESUME_GAME, newToken, "Login successful. Resuming game"));
                            output.flush();
                        }

                        default -> {
                            output.writeObject(new Message(MessageType.AUTHENTICATED, newToken, "Login successful."));
                            output.flush();
                        }
                    }
                } catch(IOException e){
                    // TODO: handle exceptions;
                }
            });
        } else {
            System.out.println("Authenticated failed");
            try {
                output.writeObject(new Message(MessageType.ERROR, token, "Login failed, Username or password incorrect."));
                output.flush();
            } catch (IOException e) {
                // TODO: handle exceptions;
            }
        }
    }

    private static String generateRandomToken() {
        Random random = new Random();
        int randomInt = random.nextInt();
        return Integer.toHexString(randomInt);
    }
}
