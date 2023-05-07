package server.game;

import protocol.Message;
import protocol.MessageType;
import server.models.Player;
import server.models.UserState;
import server.queues.GameQueue;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static server.data.UsersData.updateStateByUsername;

public class Game {
    private final List<Player> players;

    public Game(List<Player> players) {
        this.players = players;
    }

    public void start() {
        // Create a countdown latch for waiting for responses from all players
        CountDownLatch latch = new CountDownLatch(players.size());

        // Send the message to each player
        for (Player player : players) {
            try {
                player.getOutputStream().writeObject(new Message(MessageType.CHECK, player.getUser().getToken(), "Checking if user connected"));
                player.getOutputStream().flush();

                new Thread(() -> {
                    try {
                        boolean responseReceived = latch.await(1, TimeUnit.SECONDS);
                        if (!responseReceived) {
                            for (Player p : players) {
                                if (p.getUser().equals(player.getUser())) {
                                    updateStateByUsername(p.getUser().getUsername(), -1L, UserState.NONE);
                                    System.out.println("Player " + p.getUser().getUsername() + " removed from the queue.");
                                }
                            }
                        } else if (latch.getCount() == 0) {
                            for (Player p : players) {
                                p.getOutputStream().writeObject(new Message(MessageType.START_GAME, p.getUser().getToken(), "Starting the game"));
                                p.getOutputStream().flush();
                                updateStateByUsername(p.getUser().getUsername(), System.currentTimeMillis(), UserState.GAME);
                                System.out.println("Player " + p.getUser().getUsername() + " entering the game.");
                            }
                        }
                    } catch (InterruptedException | IOException e) {
                        // TODO: handle exceptions
                    }
                }).start();

            } catch (IOException e) {
                // TODO: handle exceptions
            }
        }
    }
}
