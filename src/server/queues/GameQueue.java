package server.queues;

import server.models.User;

import java.util.Iterator;

public interface GameQueue {
    void add(User user, Long time);
    int size();
    boolean contains(User user);
    Iterator<User> iterator();
}
