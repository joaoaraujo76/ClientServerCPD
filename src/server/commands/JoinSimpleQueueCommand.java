package server.commands;

import protocol.Message;
import protocol.MessageType;
import server.models.User;
import server.queues.*;
import server.repository.UsersRepository;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Optional;

public class JoinSimpleQueueCommand implements Command {
    private final Message message;
    private final ObjectOutputStream output;

    public JoinSimpleQueueCommand(Message message, ObjectOutputStream output) {
        this.message = message;
        this.output = output;
    }

    @Override
    public void execute() throws IOException {
        String token = message.getToken();

        Optional<User> userOptional = UsersRepository.getUserByToken(token);
        GameQueue queue = SimpleQueue.getInstance();
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if(!queue.contains(user)) {
                queue.add(user);
                output.writeObject(new Message(MessageType.QUEUE_POSITION, token, "You are in queue position number " + queue.size()));
                System.out.println("User " + user.getUsername() + " queued in the " + queue.getClass() + " in position " + queue.size());
            } else {
                output.writeObject(new Message(MessageType.ERROR, token, "User already queued"));
            }
        } else {
            output.writeObject(new Message(MessageType.ERROR, token, "No user for given token."));
            System.out.println("No user for given token.");
        }
    }
}
