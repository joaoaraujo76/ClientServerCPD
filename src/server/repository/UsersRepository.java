package server.repository;

import server.models.Player;
import server.models.User;
import server.queues.*;

import java.util.*;


public class UsersRepository implements Repository<User> {
    private static final Set<User> userList = new HashSet<>();

    public static Set<User> getUserList() {
        return userList;
    }

    public static Optional<User> getUserByName(String name) {
        for (User user : userList) {
            if (user.getUsername().equals(name)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public static Optional<User> getUserByToken(String token) {
        for (User user : userList) {
            if (user.getToken().equals(token)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public static void updatePlayer(Player player) {
        User user = player.getUser();
        GameQueue queue = getQueueForUser(user);

        if (queue != null) {
            Long queueJoinTime = queue.getPlayerByUser(user)
                    .map(Player::getQueueJoinTime)
                    .orElse(null);

            queue.removePlayer(player.getUser());
            queue.add(player, queueJoinTime);
        }
    }

    private static GameQueue getQueueForUser(User user) {
        List<GameQueue> queues = Arrays.asList(
                HighEloQueue.getInstance(),
                MediumEloQueue.getInstance(),
                LowEloQueue.getInstance(),
                SimpleQueue.getInstance()
        );

        for(GameQueue queue: queues) {
            if (queue.containsUser(user)) {
                return queue;
            }
        }
        return null;
    }
}
