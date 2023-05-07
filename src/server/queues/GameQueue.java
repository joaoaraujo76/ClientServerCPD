package server.queues;

import server.models.Player;

import java.util.Iterator;

public interface GameQueue {
    void add(Player player, Long time);
    int size();
    boolean contains(Player player);
    Iterator<Player> iterator();
}
