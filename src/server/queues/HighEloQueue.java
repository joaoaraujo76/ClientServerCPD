package server.queues;

import server.data.UsersData;
import server.models.Player;
import server.models.User;
import server.models.UserState;

import java.util.*;

import static server.data.GameData.gameConstants.MAX_PLAYERS;
import static server.data.UsersData.updateStateByUsername;
import static server.game.GameChecker.checkForGameStart;

public final class HighEloQueue implements GameQueue {

    private static volatile HighEloQueue instance;
    private final Queue<Player> queue;

    private HighEloQueue() {
        queue = new PriorityQueue<>(Comparator.comparingLong(Player::getQueueJoinTime));
    }

    public static HighEloQueue getInstance() {
        if (instance == null) {
            synchronized (HighEloQueue.class) {
                if (instance == null) {
                    instance = new HighEloQueue();
                }
            }
        }
        return instance;
    }

    @Override
    public void add(Player player, Long time) {
        queue.add(player);
        updateStateByUsername(player.getUser().getUsername(), time, UserState.QUEUE);
        if (queue.size() >= MAX_PLAYERS) {
            checkForGameStart(getInstance());
        }
    }

    @Override
    public void removePlayer(User user) {
        UsersData.updateStateByUsername(user.getUsername(), -1L, UserState.NONE);
        Iterator<Player> iterator = iterator();
        while (iterator.hasNext()) {
            Player p = iterator.next();
            if (p.getUser().equals(user)) {
                iterator.remove();
                break;
            }
        }
    }

    @Override
    public boolean containsUser(User user) {
        for (Player player : queue) {
            if (player.getUser().equals(user)) {
                return true;
            }
        }
        return false;
    }

    public Optional<Player> getPlayerByUser(User user) {
        for (Player player : queue) {
            if (player.getUser().equals(user)) {
                return Optional.of(player);
            }
        }
        return Optional.empty();
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

    @Override
    public Player peek() {
        return queue.peek();
    }

    @Override
    public Player poll() {
        return queue.poll();
    }

    @Override
    public boolean offer(Player player) {
        return queue.offer(player);
    }

    @Override
    public Player remove() {
        return queue.remove();
    }
}
