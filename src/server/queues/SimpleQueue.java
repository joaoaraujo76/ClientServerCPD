package server.queues;

import server.game.Game;
import server.models.Player;
import server.models.UserState;

import java.util.*;

import static server.data.UsersData.updateStateByUsername;

public class SimpleQueue implements GameQueue {
    private static volatile SimpleQueue instance;
    private final Queue<Player> queue;

    private SimpleQueue() {
        queue = new PriorityQueue<>(Comparator.comparingLong(Player::getQueueJoinTime));
    }

    public static SimpleQueue getInstance() {
        if (instance == null) {
            synchronized (LowEloQueue.class) {
                if (instance == null) {
                    instance = new SimpleQueue();
                }
            }
        }
        return instance;
    }

    @Override
    public void add(Player player, Long time) {
        queue.add(player);
        updateStateByUsername(player.getUser().getUsername(), time, UserState.QUEUE);
        checkForGameStart();
    }

    private void checkForGameStart() {
        if (queue.size() >= 2) {
            // Start a new thread to handle the game
            new Thread(() -> {
                // Get the players from the queue
                List<Player> players = new ArrayList<>();
                for (int i = 0; i < 2; i++) {
                    players.add(queue.remove());
                }
                Game game = new Game(players);
                game.start();
            }).start();
        }
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public boolean contains(Player player) {
        return queue.contains(player);
    }

    @Override
    public Iterator<Player> iterator() {
        return queue.iterator();
    }
}
