package server.queues;

import server.models.User;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class SimpleQueue implements GameQueue {
    private static volatile SimpleQueue instance;
    private final Queue<User> queue;

    private SimpleQueue() {
        queue = new PriorityQueue<>(Comparator.comparingLong(User::getExpiryDateToken));
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
