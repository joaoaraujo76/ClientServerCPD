package server.queues;

import server.models.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static server.game.GameChecker.check;

public class QueueTimeoutChecker implements Runnable {
    private final GameQueue queue;
    private final GameQueue lowerQueue;
    private final long TIMEOUT_DURATION_MS;


    public QueueTimeoutChecker(GameQueue queue, GameQueue lowerQueue, Integer TIMEOUT_MINUTES) {
        this.queue = queue;
        this.lowerQueue = lowerQueue;
        this.TIMEOUT_DURATION_MS = TIMEOUT_MINUTES * 60 * 1000;
    }

    @Override
    public void run() {
        while (true) {
            try {
                long SLEEP_DURATION_MS = 30 * 1000; // 30 seconds
                Thread.sleep(SLEEP_DURATION_MS);
            } catch (InterruptedException e) {
                //TODO: handle exception
            }
            long currentTime = System.currentTimeMillis();
            synchronized (queue) {
                Iterator<Player> iterator = queue.iterator();
                while (iterator.hasNext()) {
                    Player player = iterator.next();
                    if (currentTime - player.getUser().getQueueJoinTime() > TIMEOUT_DURATION_MS) {
                        queue.removePlayer(player.getUser());
                        lowerQueue.add(player, System.currentTimeMillis());
                        System.out.println("Moved user " + player.getUser().getUsername() + " from " + queue.getClass().toString() + " to " + lowerQueue.getClass().toString());
                    }
                }
            }
        }
    }

    public static void checkForGameStart(GameQueue queue) {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            players.add(queue.peek());
            queue.offer(queue.poll());
        }
        check(players, queue);
    }
}
