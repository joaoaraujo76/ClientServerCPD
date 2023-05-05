package server.queues;

import server.models.User;

public interface GameQueue {
    void add(User user);
    int size();
    User poll();
}