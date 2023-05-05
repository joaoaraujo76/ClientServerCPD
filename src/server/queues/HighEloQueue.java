package server.queues;

import server.models.User;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public final class HighEloQueue implements GameQueue {
    private static volatile HighEloQueue instance;
    private final Queue<User> queue;

    private HighEloQueue() {
        queue = new PriorityQueue<>(Comparator.comparingLong(User::getExpiryDateToken));
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
    public void add(User user) {
        queue.add(user);
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public boolean contains(User user) {
        return queue.contains(user);
    }
}
