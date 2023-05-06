package server.queues;

import server.models.UserState;
import server.models.User;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

import static server.data.UsersData.updateStateByUsername;

public class SimpleQueue implements GameQueue {
    private static volatile SimpleQueue instance;
    private final Queue<User> queue;

    private SimpleQueue() {
        queue = new PriorityQueue<>(Comparator.comparingLong(User::getQueueJoinTime));
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
