package server.queues;

import server.models.Player;
import server.models.UserState;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

import static server.data.UsersData.updateStateByUsername;

public final class MediumEloQueue implements GameQueue {
    private static volatile MediumEloQueue instance;
    private final Queue<Player> queue;

    private MediumEloQueue() {
        queue = new PriorityQueue<>(Comparator.comparingLong(Player::getQueueJoinTime));
    }

    public static MediumEloQueue getInstance() {
        if (instance == null) {
            synchronized (MediumEloQueue.class) {
                if (instance == null) {
                    instance = new MediumEloQueue();
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
