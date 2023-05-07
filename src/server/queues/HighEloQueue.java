package server.queues;

import server.models.Player;
import server.models.UserState;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

import static server.data.UsersData.updateStateByUsername;

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
    }

    @Override
    public void removePlayer(Player player) {
        Iterator<Player> iterator = iterator();
        while (iterator.hasNext()) {
            if (iterator.equals(player)) {
                iterator.remove();
            }
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
