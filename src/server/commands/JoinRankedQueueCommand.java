package server.commands;

import protocol.Message;
import protocol.MessageType;
import server.models.Player;
import server.models.UserState;
import server.models.User;
import server.queues.GameQueue;
import server.queues.HighEloQueue;
import server.queues.LowEloQueue;
import server.queues.MediumEloQueue;
import server.repository.UsersRepository;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Optional;

public class JoinRankedQueueCommand implements Command {
    private final Message message;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;
    private final Socket socket;

    public JoinRankedQueueCommand(Message message, ObjectInputStream input, ObjectOutputStream output, Socket socket) {
        this.message = message;
        this.input = input;
        this.output = output;
        this.socket = socket;
    }

    @Override
    public void run() {
        String token = message.getToken();

        Optional<User> userOptional = UsersRepository.getUserByToken(token);
        GameQueue queue;
        try {
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                Player player = new Player(user, socket, input, output);

                int elo = user.getElo();

                if (elo >= 2000) {
                    queue = HighEloQueue.getInstance();
                } else if (elo >= 1500) {
                    queue = MediumEloQueue.getInstance();
                } else {
                    queue = LowEloQueue.getInstance();
                }

                if (!queue.contains(player) && !user.getState().equals(UserState.QUEUE)) {
                    int position = queue.size() + 1;

                    output.writeObject(new Message(MessageType.QUEUED, token, "You are in queue position number " + position));
                    output.flush();

                    queue.add(player, System.currentTimeMillis());
                    System.out.println("User " + user.getUsername() + " queued in the " + queue.getClass() + " in position " + position);

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
        } catch (IOException e) {
            // TODO: handle exceptions;
        }
    }
}
