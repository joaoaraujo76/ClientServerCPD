package server.commands;

import protocol.Message;
import protocol.MessageType;
import server.models.User;

import java.io.ObjectOutputStream;
import java.util.Optional;

import static server.repository.UsersRepository.getUserByToken;


public class SeeEloCommand implements Command {

    private final Message message;
    private final ObjectOutputStream output;

    public SeeEloCommand(Message message, ObjectOutputStream output) {
        this.message = message;
        this.output = output;
    }

    @Override
    public void run() {
        String token = message.getToken();

        Optional<User> user = getUserByToken(token);

        user.ifPresent(u -> {
            try {

                int elo = u.getElo();

                System.out.println("Elo: " + elo);

                String Difficulty;

                if (elo >= 2000) {
                    Difficulty = "Hard";
                } else if (elo >= 1500) {
                    Difficulty = "Medium";
                } else {
                    Difficulty = "Easy";
                }

                System.out.println("Difficulty: " + Difficulty);

                output.writeObject(new Message(MessageType.SEE_ELO, token, "Your Elo is: " + elo +  ". You are in " + Difficulty + " level."));
                output.flush();
            } catch (Exception e) {
                //TODO: handle exception
            }
        });
    }
}
