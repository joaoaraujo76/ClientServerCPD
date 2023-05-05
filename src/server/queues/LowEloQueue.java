package server.queues;

import server.models.User;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public final class LowEloQueue implements GameQueue {
    private static volatile LowEloQueue instance;

    private LowEloQueue() {
        Queue<User> queue = new PriorityQueue<>(Comparator.comparingLong(User::getExpiryDateToken));
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
    public void add(User user) {
        instance.add(user);
    }

    @Override
    public int size() {
        return instance.size();
    }

    @Override
    public User poll() {
        return instance.poll();
    }
}
