package server.queues;

import server.data.UsersData;
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
    }

    @Override
    public void removePlayer(Player player) {
        UsersData.updateStateByUsername(player.getUser().getUsername(), -1L, UserState.NONE);
        Iterator<Player> iterator = iterator();
        while (iterator.hasNext()) {
            Player p = iterator.next();
            if (p.equals(player)) {
                iterator.remove();
                break;
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
}
