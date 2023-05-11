package server.game;

import protocol.Message;
import protocol.MessageType;
import server.models.Player;
import server.queues.GameQueue;

import java.io.IOException;
import java.util.List;

public class GameChecker {

    public synchronized static void check(List<Player> players, GameQueue queue) {
        int checkedPlayers = 0;

        for (Player player : players) {
            if (player.getSocket().isConnected()) {
                checkedPlayers++;
            } else {
                queue.removePlayer(player);
            }
        }

        // If all players have responded, start the game
        if (checkedPlayers == players.size()) {
            try {
                for (Player player : players) {
                    player.getOutputStream().writeObject(new Message(MessageType.START_GAME, player.getUser().getToken(), "Checking if user connected"));
                    player.getOutputStream().flush();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                for (Player player : players) {
                    player.getOutputStream().writeObject(new Message(MessageType.ERROR, player.getUser().getToken(), "Checking if user connected"));
                    player.getOutputStream().flush();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
