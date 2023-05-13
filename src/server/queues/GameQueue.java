package server.queues;

import server.models.Player;
import server.models.User;

import java.util.Iterator;
import java.util.Optional;

public interface GameQueue {
    void add(Player player, Long time);
    void removePlayer(User user);

    boolean containsUser(User user);
    Optional<Player> getPlayerByUser(User user);

    int size();
    boolean contains(Player player);
    Iterator<Player> iterator();

    Player peek();

    Player poll();

    boolean offer(Player player);
}
