package server.commands;

import protocol.Message;
import protocol.MessageType;
import server.services.UserAuthenticator;

import java.io.IOException;
import java.io.ObjectOutputStream;
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
    public void execute() throws IOException {
        String username = message.getUsername();
        String password = message.getPassword();
        String token = message.getToken();

        System.out.println("Login -----------");
        System.out.println("Username: " + username);

        if (UserAuthenticator.login(username, password)) {
            String newToken = generateRandomToken();
            updateTokenByUsername(username, newToken);
            System.out.println("Authenticated succeeded\n");
            output.writeObject(new Message(MessageType.AUTHENTICATED, newToken, "Login successful."));
            output.flush();
        } else {
            System.out.println("Authenticated failed");
            output.writeObject(new Message(MessageType.ERROR, token, "Login failed, Username or password incorrect."));
            output.flush();
        }
    }

    private static String generateRandomToken() {
        Random random = new Random();
        int randomInt = random.nextInt();
        return Integer.toHexString(randomInt);
    }
}
