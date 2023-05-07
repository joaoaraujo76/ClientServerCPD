package server.game;

import protocol.Message;
import protocol.MessageType;
import server.models.Player;

import java.io.IOException;
import java.util.List;

public class Game {
    private final List<Player> players;

    public Game(List<Player> players) {
        this.players = players;
    }

    public void start() {
        for(Player player : players) {
            try {
                player.getOutputStream().writeObject(new Message(MessageType.GAME, player.getUser().getToken(), "Starting the game"));
                player.getOutputStream().flush();
            } catch (IOException e) {
                // TODO: handle exceptions
            }
        }
    }
}
