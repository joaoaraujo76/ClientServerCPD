package server.commands;

import protocol.Message;
import protocol.MessageType;
import server.data.UsersData;
import server.models.User;

import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import static server.authenticator.UserAuthenticator.hashPassword;
import static server.repository.UsersRepository.getUserByToken;

public class ChangePasswordCommand implements Command {
    private final Message message;
    private final ObjectOutputStream output;

    public ChangePasswordCommand(Message message, ObjectOutputStream output) {
        this.message = message;
        this.output = output;
    }

    @Override
    public void execute() {
        String password = message.getMessage();
        String token = message.getToken();

        try {
            Optional<User> user = getUserByToken(token);
            String hashedPassword = hashPassword(password);
            user.ifPresent(u -> {
                try {
                    UsersData.updatePasswordByUsername(u.getUsername(), hashedPassword);
                    System.out.println("Password change succeeded");
                    output.writeObject(new Message(MessageType.PASSWORD_CHANGED, token, "Registration successful."));
                    output.flush();
                } catch (Exception e) {
                    //TODO: handle exception
                }
            });
        } catch (NoSuchAlgorithmException e) {
            //TODO: handle exception
        }
    }
}
