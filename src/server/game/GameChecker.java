package server.game;

import protocol.Message;
import protocol.MessageType;
import server.models.Player;
import server.models.UserState;
import server.queues.GameQueue;
import server.queues.SimpleQueue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import static server.data.UsersData.updateStateByUsername;
import static server.data.GameData.gameConstants.MAX_PLAYERS;

public class GameChecker {
    private static final int MAX_ACTIVE_GAMES = 5;
    private static final ExecutorService gameExecutor = Executors.newFixedThreadPool(MAX_ACTIVE_GAMES);

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
            //Create a new thread in the fixed thread pool
            gameExecutor.execute(() -> {
                try {
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

                    String gameType;
                    if (queue.getClass().equals(SimpleQueue.class)) {
                        gameType = "UNRANKED";
                    } else {
                        gameType = "RANKED";
                    }

                    new Game(players, gameType).run();
                } catch (Exception e) {
                    // Handle any exceptions thrown during the game
                    System.out.println("Max amount of game threads reached.");
                    e.printStackTrace();
                }
            });

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
        for (int i = 0; i < MAX_PLAYERS; i++) {
            players.add(queue.remove());
        }
        check(players, queue);
    }
}
