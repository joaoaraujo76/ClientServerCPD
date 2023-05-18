package server.commands;

import protocol.Message;
import protocol.MessageType;
import server.authenticator.UserAuthenticator;
import server.data.UsersData;
import server.models.User;
import server.queues.HighEloQueue;
import server.queues.LowEloQueue;
import server.queues.MediumEloQueue;
import server.repository.UsersRepository;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import static server.authenticator.UserAuthenticator.hashPassword;
import static server.repository.Repository.getUserByName;
import static server.repository.UsersRepository.getUserByToken;


public class SeeEloCommand implements Command {

    private final Message message;
    private final ObjectOutputStream output;

    public SeeEloCommand(Message message, ObjectOutputStream output) {
        this.message = message;
        this.output = output;
    }

    @Override
    public void execute() {
        String token = message.getToken();

        Optional<User> user = getUserByToken(token);

        user.ifPresent(u -> {
            try {

                Integer elo = u.getElo();

                System.out.println("Elo: " + elo);

                String Diffuculty;

                if (elo >= 2000) {
                    Diffuculty = "Hard";
                } else if (elo >= 1500) {
                    Diffuculty = "Medium";
                } else {
                    Diffuculty = "Easy";
                }

                System.out.println("Difficuly: " + Diffuculty);

                output.writeObject(new Message(MessageType.SEE_ELO, token, "Your Elo is: " + Integer.toString(elo) +  ". You are in " + Diffuculty + " level."));
                output.flush();
            } catch (Exception e) {

                //TODO: handle exception
            }
        });

    }

}
