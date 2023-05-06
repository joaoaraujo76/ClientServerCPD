package server.queues;

import server.models.UserState;
import server.models.User;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

import static server.data.UsersData.updateStateByUsername;

public final class LowEloQueue implements GameQueue {
    private static volatile LowEloQueue instance;
    private final Queue<User> queue;
    private LowEloQueue() {
        queue = new PriorityQueue<>(Comparator.comparingLong(User::getExpiryDateToken));
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
    public void add(User user, Long time) {
        queue.add(user);
        updateStateByUsername(user.getUsername(), time, UserState.QUEUE);
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
