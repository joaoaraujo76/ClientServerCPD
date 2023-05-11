package server.queues;

import server.models.Player;

import java.util.Iterator;

public interface GameQueue {
    void add(Player player, Long time);
    void removePlayer(Player player);
    int size();
    boolean contains(Player player);
    Iterator<Player> iterator();

    Player peek();

    Player poll();

    boolean offer(Player player);
}
