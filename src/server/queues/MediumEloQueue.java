package server.queues;

import server.models.User;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public final class MediumEloQueue implements GameQueue {
    private static volatile MediumEloQueue instance;

    private MediumEloQueue() {
        Queue<User> queue = new PriorityQueue<>(Comparator.comparingLong(User::getExpiryDateToken));
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
