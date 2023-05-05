package server.queues;

import server.models.GameState;
import server.models.User;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

import static server.data.UsersData.updateStateByUsername;

public final class MediumEloQueue implements GameQueue {
    private static volatile MediumEloQueue instance;
    private final Queue<User> queue;

    private MediumEloQueue() {
        queue = new PriorityQueue<>(Comparator.comparingLong(User::getExpiryDateToken));
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
    public void add(User user, Long time) {
        queue.add(user);
        updateStateByUsername(user.getUsername(), time, GameState.QUEUE);
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public boolean contains(User user) {
        return queue.contains(user);
    }

    @Override
    public Iterator<User> iterator() {
        return queue.iterator();
    }
}
