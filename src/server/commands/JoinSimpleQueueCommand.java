package server.commands;

import protocol.Message;
import protocol.MessageType;
import server.models.Player;
import server.models.UserState;
import server.models.User;
import server.queues.*;
import server.repository.UsersRepository;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Optional;

public class JoinSimpleQueueCommand implements Command {
    private final Message message;
    private final ObjectOutputStream output;
    private final Socket socket;

    public JoinSimpleQueueCommand(Message message, ObjectOutputStream output, Socket socket) {
        this.message = message;
        this.output = output;
        this.socket = socket;
    }

    @Override
    public void execute() throws IOException {
        String token = message.getToken();

        Optional<User> userOptional = UsersRepository.getUserByToken(token);
        GameQueue queue = SimpleQueue.getInstance();
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Player player = new Player(user, output);

            if(!queue.contains(player) && !user.getState().equals(UserState.QUEUE)) {
                queue.add(player, System.currentTimeMillis());
                System.out.println("User " + user.getUsername() + " queued in the " + queue.getClass() + " in position " + queue.size());
                output.writeObject(new Message(MessageType.QUEUED, token, "You are in queue position number " + queue.size()));
                output.flush();
            } else {
                System.out.println("User already queued.");
                output.writeObject(new Message(MessageType.ERROR, token, "User already queued."));
                output.flush();
            }
        } else {
            System.out.println("No user for given token.");
            output.writeObject(new Message(MessageType.ERROR, token, "No user for given token."));
            output.flush();
        }
    }
}
