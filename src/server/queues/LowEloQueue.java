package server.queues;

import server.models.Player;
import server.models.UserState;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

import static server.data.UsersData.updateStateByUsername;

public final class LowEloQueue implements GameQueue {
    private static volatile LowEloQueue instance;
    private final Queue<Player> queue;
    private LowEloQueue() {
        queue = new PriorityQueue<>(Comparator.comparingLong(Player::getQueueJoinTime));
    }

    public static LowEloQueue getInstance() {
        if (instance == null) {
            synchronized (LowEloQueue.class) {
                if (instance == null) {
                    instance = new LowEloQueue();
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
