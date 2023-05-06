package server.commands;

import protocol.Message;
import protocol.MessageType;
import server.models.UserState;
import server.models.User;
import server.queues.GameQueue;
import server.queues.HighEloQueue;
import server.queues.LowEloQueue;
import server.queues.MediumEloQueue;
import server.repository.UsersRepository;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Optional;

public class JoinRankedQueueCommand implements Command {
    private final Message message;
    private final ObjectOutputStream output;

    public JoinRankedQueueCommand(Message message, ObjectOutputStream output) {
        this.message = message;
        this.output = output;
    }

    @Override
    public void execute() throws IOException {
        String token = message.getToken();

        Optional<User> userOptional = UsersRepository.getUserByToken(token);
        GameQueue queue;
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            int elo = user.getElo();

            if (elo >= 2000) {
                queue = HighEloQueue.getInstance();
            } else if (elo >= 1500) {
                queue = MediumEloQueue.getInstance();
            } else {
                queue = LowEloQueue.getInstance();
            }

            if(!queue.contains(user) && !user.getState().equals(UserState.QUEUE)) {
                queue.add(user, System.currentTimeMillis());
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
