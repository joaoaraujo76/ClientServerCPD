package server.game;

import protocol.Message;
import protocol.MessageType;
import server.models.Player;
import server.models.UserState;
import server.queues.GameQueue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static server.data.UsersData.updateStateByUsername;

public class GameChecker {

    public synchronized static void check(List<Player> players, GameQueue queue) {
        int checkedPlayers = 0;

        for (Player player : players) {
            if (player.getSocket().isConnected()) {
                checkedPlayers++;
            } else {
                queue.removePlayer(player.getUser());
            }
        }

        // If all players have responded, start the game
        if (checkedPlayers == players.size()) {
            System.out.println("Starting game");
            try {
                for (Player player : players) {
                    updateStateByUsername(player.getUser().getUsername(), -1L, UserState.GAME);
                    player.getOutputStream().writeObject(new Message(MessageType.START_GAME, player.getUser().getToken(), "Starting the game"));
                    player.getOutputStream().flush();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Thread game = new Thread(new Game(players));
            game.start();

        } else {
            System.out.println("Matchmaking aborted due to disconnected player");
            try {
                for (Player player : players) {
                    updateStateByUsername(player.getUser().getUsername(), -1L, UserState.NONE);
                    player.getOutputStream().writeObject(new Message(MessageType.ERROR, player.getUser().getToken(), "Matchmaking aborted. Returning to main menu"));
                    player.getOutputStream().flush();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void checkForGameStart(GameQueue queue) {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            players.add(queue.remove());
        }
        check(players, queue);
    }
}
