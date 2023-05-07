package server.game;

import protocol.Message;
import protocol.MessageType;
import server.models.Player;
import server.queues.GameQueue;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GameChecker {
    private static List<Player> players;
    private static GameQueue queue;

    public GameChecker(List<Player> players, GameQueue queue) {
        this.players = players;
        this.queue = queue;
    }

    public synchronized static void check() {
        // Create a countdown latch for waiting for responses from all players
        CountDownLatch latch = new CountDownLatch(players.size());

        // Send the message to each player
        for (Player player : players) {
            try {
                player.getOutputStream().writeObject(new Message(MessageType.CHECK, player.getUser().getToken(), "Checking if user connected"));
                player.getOutputStream().flush();

                // Start a separate thread for waiting for the response from this player
                new Thread(() -> {
                    try {
                        // Wait for a response for x seconds
                        boolean responseReceived = latch.await(1, TimeUnit.SECONDS);
                        if (!responseReceived) {
                            // Remove the player from the queue if no response is received
                            queue.removePlayer(player);
                        } else if (latch.getCount() == 0) {
                            // If responses are received from all players, start the game
                            Game game = new Game(players);
                            game.start();
                        }
                    } catch (InterruptedException e) {
                        // TODO: handle exceptions
                    }
                }).start();

            } catch (IOException e) {
                // TODO: handle exceptions
            }
        }

        // Wait for responses from all players
        try {
            latch.await();
        } catch (InterruptedException e) {
            // TODO: handle exceptions
        }
    }
}
